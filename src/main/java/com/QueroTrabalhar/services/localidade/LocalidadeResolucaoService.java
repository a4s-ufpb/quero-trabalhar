package com.QueroTrabalhar.services.localidade;

import com.QueroTrabalhar.domain.entity.localidade.Cidade;
import com.QueroTrabalhar.domain.entity.localidade.Estado;
import com.QueroTrabalhar.domain.entity.localidade.Localidade;
import com.QueroTrabalhar.domain.entity.localidade.LocalidadePendente;
import com.QueroTrabalhar.domain.entity.localidade.Pais;
import com.QueroTrabalhar.infrastructure.client.google.GoogleMapsClient;
import com.QueroTrabalhar.infrastructure.client.google.dto.AddressComponent;
import com.QueroTrabalhar.infrastructure.client.google.dto.GoogleGeocodeResponse;
import com.QueroTrabalhar.infrastructure.client.google.dto.GoogleResult;
import com.QueroTrabalhar.repository.CidadeRepository;
import com.QueroTrabalhar.repository.EstadoRepository;
import com.QueroTrabalhar.repository.LocalidadePendenteRepository;
import com.QueroTrabalhar.repository.PaisRepository;
import com.QueroTrabalhar.services.exceptions.BusinessRuleException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class LocalidadeResolucaoService {

    private static final String MOTIVO_LOCALIDADE_NAO_ENCONTRADA =
            "Localidade não encontrada na base interna ou na integração externa.";
    private static final String MOTIVO_FALHA_INTEGRACAO_EXTERNA =
            "Não foi possível validar a localidade na integração externa.";

    private final CidadeRepository cidadeRepository;
    private final EstadoRepository estadoRepository;
    private final PaisRepository paisRepository;
    private final LocalidadePendenteRepository localidadePendenteRepository;
    private final GoogleMapsClient googleMapsClient;

    @Value("${google.maps.retry.max-attempts:2}")
    private int googleMapsRetryMaxAttempts = 2;

    @Value("${google.maps.retry.delay-ms:200}")
    private long googleMapsRetryDelayMs = 200L;

    public LocalidadeResolucaoService(CidadeRepository cidadeRepository,
                                      EstadoRepository estadoRepository,
                                      PaisRepository paisRepository,
                                      LocalidadePendenteRepository localidadePendenteRepository,
                                      GoogleMapsClient googleMapsClient) {
        this.cidadeRepository = cidadeRepository;
        this.estadoRepository = estadoRepository;
        this.paisRepository = paisRepository;
        this.localidadePendenteRepository = localidadePendenteRepository;
        this.googleMapsClient = googleMapsClient;
    }

    @Transactional
    public ResultadoResolucaoLocalidade resolver(String textoLivre) {
        String textoNormalizado = normalizarTextoObrigatorio(textoLivre);

        Optional<Localidade> localidadeInterna = resolverNaBaseInterna(textoNormalizado);
        if (localidadeInterna.isPresent()) {
            return ResultadoResolucaoLocalidade.resolvida(localidadeInterna.get());
        }

        ResultadoTentativaExterna tentativaExterna = resolverNaIntegracaoExterna(textoNormalizado);
        if (tentativaExterna.localidade().isPresent()) {
            return ResultadoResolucaoLocalidade.resolvida(tentativaExterna.localidade().get());
        }

        LocalidadePendente localidadePendente = LocalidadePendente.criarPendenteInformadaPeloUsuario(
                textoNormalizado,
                tentativaExterna.motivoPendencia()
        );

        return ResultadoResolucaoLocalidade.pendente(localidadePendenteRepository.save(localidadePendente));
    }

    private Optional<Localidade> resolverNaBaseInterna(String textoNormalizado) {
        List<Cidade> cidades = cidadeRepository.findAllByNomeIgnoreCase(textoNormalizado);
        if (cidades.size() == 1) {
            Cidade cidade = cidades.getFirst();
            Estado estado = cidade.getEstado();
            return Optional.of(new Localidade(estado.getPais(), estado, cidade));
        }

        List<Estado> estados = estadoRepository.findAllByNomeIgnoreCase(textoNormalizado);
        if (estados.size() == 1) {
            Estado estado = estados.getFirst();
            return Optional.of(new Localidade(estado.getPais(), estado));
        }

        return paisRepository.findFirstByNomeIgnoreCase(textoNormalizado)
                .map(Localidade::new);
    }

    private ResultadoTentativaExterna resolverNaIntegracaoExterna(String textoNormalizado) {
        int maxAttempts = Math.max(1, googleMapsRetryMaxAttempts);

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            ResultadoConsultaGoogle resultadoConsulta = consultarGoogle(textoNormalizado);
            if (!resultadoConsulta.deveTentarNovamente() || attempt == maxAttempts) {
                return resultadoConsulta.resultado();
            }

            if (!aguardarRetry()) {
                return ResultadoTentativaExterna.pendente(MOTIVO_FALHA_INTEGRACAO_EXTERNA);
            }
        }

        return ResultadoTentativaExterna.pendente(MOTIVO_FALHA_INTEGRACAO_EXTERNA);
    }

    private ResultadoConsultaGoogle consultarGoogle(String textoNormalizado) {
        Optional<GoogleGeocodeResponse> responseOptional;
        try {
            responseOptional = googleMapsClient.buscarLugarComFiltro(textoNormalizado, null);
        } catch (RuntimeException exception) {
            return ResultadoConsultaGoogle.retryFalhaExterna();
        }

        if (responseOptional.isEmpty()) {
            return ResultadoConsultaGoogle.retryFalhaExterna();
        }

        GoogleGeocodeResponse response = responseOptional.get();
        if ("ZERO_RESULTS".equalsIgnoreCase(response.status())) {
            return ResultadoConsultaGoogle.pendente(MOTIVO_LOCALIDADE_NAO_ENCONTRADA);
        }

        List<GoogleResult> resultados = response.results();
        if (resultados == null || resultados.isEmpty()) {
            return ResultadoConsultaGoogle.pendente(MOTIVO_LOCALIDADE_NAO_ENCONTRADA);
        }

        if (!response.isSucesso()) {
            return ResultadoConsultaGoogle.pendente(MOTIVO_FALHA_INTEGRACAO_EXTERNA);
        }

        return converterResultadoGoogle(resultados.getFirst())
                .map(ResultadoConsultaGoogle::resolvida)
                .orElseGet(() -> ResultadoConsultaGoogle.pendente(MOTIVO_LOCALIDADE_NAO_ENCONTRADA));
    }

    private boolean aguardarRetry() {
        if (googleMapsRetryDelayMs <= 0) {
            return true;
        }

        try {
            Thread.sleep(googleMapsRetryDelayMs);
            return true;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    private Optional<Localidade> converterResultadoGoogle(GoogleResult resultadoGoogle) {
        AddressComponent componentePais = extrairComponente(resultadoGoogle, "country");
        if (!componenteValido(componentePais) || !textoPreenchido(componentePais.shortName())) {
            return Optional.empty();
        }

        Pais pais = obterOuCriarPais(componentePais);
        AddressComponent componenteEstado = extrairComponente(resultadoGoogle, "administrative_area_level_1");
        AddressComponent componenteCidade = extrairComponente(
                resultadoGoogle,
                "locality",
                "administrative_area_level_2"
        );

        if (componenteCidade != null && !componenteValido(componenteEstado)) {
            return Optional.empty();
        }

        if (!componenteValido(componenteEstado)) {
            return Optional.of(new Localidade(pais));
        }

        Estado estado = obterOuCriarEstado(componenteEstado, pais);
        if (!componenteValido(componenteCidade)) {
            return Optional.of(new Localidade(pais, estado));
        }

        Cidade cidade = obterOuCriarCidade(componenteCidade, estado);
        return Optional.of(new Localidade(pais, estado, cidade));
    }

    private Pais obterOuCriarPais(AddressComponent componentePais) {
        String nomePais = componentePais.longName().trim();
        String siglaPais = componentePais.shortName().trim().toUpperCase();

        return paisRepository.findFirstBySiglaIgnoreCase(siglaPais)
                .or(() -> paisRepository.findFirstByNomeIgnoreCase(nomePais))
                .orElseGet(() -> paisRepository.save(new Pais(nomePais, siglaPais)));
    }

    private Estado obterOuCriarEstado(AddressComponent componenteEstado, Pais pais) {
        String nomeEstado = componenteEstado.longName().trim();
        String siglaEstado = normalizarTextoOpcional(componenteEstado.shortName());

        return estadoRepository.findFirstByNomeIgnoreCaseAndPais(nomeEstado, pais)
                .orElseGet(() -> estadoRepository.save(new Estado(nomeEstado, siglaEstado, pais)));
    }

    private Cidade obterOuCriarCidade(AddressComponent componenteCidade, Estado estado) {
        String nomeCidade = componenteCidade.longName().trim();

        return cidadeRepository.findFirstByNomeIgnoreCaseAndEstado(nomeCidade, estado)
                .orElseGet(() -> cidadeRepository.save(new Cidade(nomeCidade, estado)));
    }

    private AddressComponent extrairComponente(GoogleResult resultado, String... tiposDesejados) {
        List<String> tiposAlvo = List.of(tiposDesejados);
        if (resultado.addressComponents() == null) {
            return null;
        }

        for (AddressComponent componente : resultado.addressComponents()) {
            if (componente.types() == null) {
                continue;
            }

            for (String tipo : componente.types()) {
                if (tiposAlvo.contains(tipo)) {
                    return componente;
                }
            }
        }

        return null;
    }

    private String normalizarTextoObrigatorio(String textoLivre) {
        String textoNormalizado = normalizarTextoOpcional(textoLivre);
        if (textoNormalizado == null) {
            throw new BusinessRuleException("O texto da localidade é obrigatório.");
        }
        return textoNormalizado;
    }

    private String normalizarTextoOpcional(String valor) {
        if (valor == null) {
            return null;
        }

        String valorNormalizado = valor.trim();
        return valorNormalizado.isBlank() ? null : valorNormalizado;
    }

    private boolean componenteValido(AddressComponent componente) {
        return componente != null && textoPreenchido(componente.longName());
    }

    private boolean textoPreenchido(String valor) {
        return valor != null && !valor.isBlank();
    }

    private record ResultadoTentativaExterna(Optional<Localidade> localidade, String motivoPendencia) {

        private static ResultadoTentativaExterna resolvida(Localidade localidade) {
            return new ResultadoTentativaExterna(Optional.of(localidade), null);
        }

        private static ResultadoTentativaExterna pendente(String motivoPendencia) {
            return new ResultadoTentativaExterna(Optional.empty(), motivoPendencia);
        }
    }

    private record ResultadoConsultaGoogle(ResultadoTentativaExterna resultado, boolean deveTentarNovamente) {

        private static ResultadoConsultaGoogle resolvida(Localidade localidade) {
            return new ResultadoConsultaGoogle(ResultadoTentativaExterna.resolvida(localidade), false);
        }

        private static ResultadoConsultaGoogle pendente(String motivoPendencia) {
            return new ResultadoConsultaGoogle(ResultadoTentativaExterna.pendente(motivoPendencia), false);
        }

        private static ResultadoConsultaGoogle retryFalhaExterna() {
            return new ResultadoConsultaGoogle(
                    ResultadoTentativaExterna.pendente(MOTIVO_FALHA_INTEGRACAO_EXTERNA),
                    true
            );
        }
    }
}
