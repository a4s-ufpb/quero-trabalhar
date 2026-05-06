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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class LocalidadeResolucaoService {

    private static final Logger logger = LoggerFactory.getLogger(LocalidadeResolucaoService.class);

    private static final String MOTIVO_LOCALIDADE_NAO_ENCONTRADA =
            "Localidade n\u00E3o encontrada na base interna ou na integra\u00E7\u00E3o externa.";
    private static final String MOTIVO_FALHA_INTEGRACAO_EXTERNA =
            "N\u00E3o foi poss\u00EDvel validar a localidade na integra\u00E7\u00E3o externa.";
    private static final String MOTIVO_LOCALIDADE_AMBIGUA_BASE_INTERNA =
            "A localidade informada corresponde a m\u00FAltiplas op\u00E7\u00F5es na base interna e precisa de confirma\u00E7\u00E3o do usu\u00E1rio.";
    private static final String MOTIVO_LOCALIDADE_AMBIGUA_GOOGLE =
            "A localidade informada retornou m\u00FAltiplas op\u00E7\u00F5es no Google Maps e precisa de confirma\u00E7\u00E3o do usu\u00E1rio.";

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
        long inicioResolucao = System.nanoTime();
        String textoNormalizado = normalizarTextoObrigatorio(textoLivre);
        String textoHash = gerarTextoHash(textoNormalizado);

        ResultadoConsultaInterna resultadoInterno = resolverNaBaseInterna(textoNormalizado, textoHash);
        if (resultadoInterno.localidade().isPresent()) {
            Localidade localidade = resultadoInterno.localidade().get();
            logger.info(
                    "event=localidade_resolvida origem=BASE_INTERNA textoHash={} nivel={} duracaoMs={}",
                    textoHash,
                    determinarNivel(localidade),
                    calcularDuracaoMs(inicioResolucao)
            );
            return ResultadoResolucaoLocalidade.resolvida(localidade);
        }
        if (resultadoInterno.motivoPendencia() != null) {
            return criarResultadoPendente(
                    textoNormalizado,
                    textoHash,
                    resultadoInterno.motivoPendencia(),
                    inicioResolucao,
                    0
            );
        }

        int maxAttempts = Math.max(1, googleMapsRetryMaxAttempts);
        logger.info(
                "event=localidade_google_necessaria textoHash={} maxTentativas={}",
                textoHash,
                maxAttempts
        );

        ResultadoTentativaExterna tentativaExterna = resolverNaIntegracaoExterna(
                textoNormalizado,
                textoHash,
                maxAttempts
        );
        if (tentativaExterna.localidade().isPresent()) {
            Localidade localidade = tentativaExterna.localidade().get();
            logger.info(
                    "event=localidade_resolvida origem=GOOGLE_MAPS textoHash={} nivel={} tentativasExecutadas={} duracaoMs={}",
                    textoHash,
                    determinarNivel(localidade),
                    tentativaExterna.tentativasExecutadas(),
                    calcularDuracaoMs(inicioResolucao)
            );
            return ResultadoResolucaoLocalidade.resolvida(localidade);
        }

        return criarResultadoPendente(
                textoNormalizado,
                textoHash,
                tentativaExterna.motivoPendencia(),
                inicioResolucao,
                tentativaExterna.tentativasExecutadas()
        );
    }

    private ResultadoResolucaoLocalidade criarResultadoPendente(String textoNormalizado,
                                                                String textoHash,
                                                                String motivoPendencia,
                                                                long inicioResolucao,
                                                                int tentativasExecutadas) {
        LocalidadePendente localidadePendente = LocalidadePendente.criarPendenteInformadaPeloUsuario(
                textoNormalizado,
                motivoPendencia
        );
        LocalidadePendente localidadePendenteSalva = localidadePendenteRepository.save(localidadePendente);

        logger.warn(
                "event=localidade_pendente_criada textoHash={} motivoCategoria={} tentativasExecutadas={} statusValidacao={} origem={} duracaoMs={}",
                textoHash,
                categorizarMotivoPendencia(motivoPendencia),
                tentativasExecutadas,
                localidadePendenteSalva.getStatusValidacao(),
                localidadePendenteSalva.getOrigem(),
                calcularDuracaoMs(inicioResolucao)
        );

        return ResultadoResolucaoLocalidade.pendente(localidadePendenteSalva);
    }

    private ResultadoConsultaInterna resolverNaBaseInterna(String textoNormalizado, String textoHash) {
        List<Cidade> cidades = cidadeRepository.findAllByNomeIgnoreCase(textoNormalizado);
        List<Estado> estados = estadoRepository.findAllByNomeIgnoreCase(textoNormalizado);
        Optional<Pais> paisOptional = paisRepository.findFirstByNomeIgnoreCase(textoNormalizado);

        int quantidadePossibilidades = cidades.size() + estados.size() + (paisOptional.isPresent() ? 1 : 0);
        if (quantidadePossibilidades > 1) {
            logger.warn(
                    "event=localidade_base_interna_ambigua textoHash={} quantidadeCidades={} quantidadeEstados={} quantidadePaises={} quantidadePossibilidades={} acao=PENDENCIA",
                    textoHash,
                    cidades.size(),
                    estados.size(),
                    paisOptional.isPresent() ? 1 : 0,
                    quantidadePossibilidades
            );
            return ResultadoConsultaInterna.ambigua();
        }

        if (cidades.size() == 1) {
            Cidade cidade = cidades.getFirst();
            Estado estado = cidade.getEstado();
            return ResultadoConsultaInterna.resolvida(new Localidade(estado.getPais(), estado, cidade));
        }
        if (estados.size() == 1) {
            Estado estado = estados.getFirst();
            return ResultadoConsultaInterna.resolvida(new Localidade(estado.getPais(), estado));
        }
        if (paisOptional.isPresent()) {
            return ResultadoConsultaInterna.resolvida(new Localidade(paisOptional.get()));
        }

        logger.info("event=localidade_base_interna_sem_match textoHash={}", textoHash);

        return ResultadoConsultaInterna.semMatch();
    }

    private ResultadoTentativaExterna resolverNaIntegracaoExterna(String textoNormalizado,
                                                                  String textoHash,
                                                                  int maxAttempts) {
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            logger.info(
                    "event=localidade_google_tentativa textoHash={} tentativa={} maxTentativas={}",
                    textoHash,
                    attempt,
                    maxAttempts
            );

            ResultadoConsultaGoogle resultadoConsulta = consultarGoogle(textoNormalizado, textoHash, attempt);
            if (!resultadoConsulta.deveTentarNovamente() || attempt == maxAttempts) {
                return resultadoConsulta.toResultadoTentativaExterna(attempt);
            }

            logger.warn(
                    "event=localidade_google_retry textoHash={} tentativaAtual={} proximaTentativa={} motivo=FALHA_TRANSITORIA_OU_RESPOSTA_AUSENTE",
                    textoHash,
                    attempt,
                    attempt + 1
            );

            if (!aguardarRetry(textoHash, attempt, maxAttempts)) {
                return ResultadoTentativaExterna.pendente(MOTIVO_FALHA_INTEGRACAO_EXTERNA, attempt);
            }
        }

        return ResultadoTentativaExterna.pendente(MOTIVO_FALHA_INTEGRACAO_EXTERNA, maxAttempts);
    }

    private ResultadoConsultaGoogle consultarGoogle(String textoNormalizado, String textoHash, int attempt) {
        long inicioConsulta = System.nanoTime();
        Optional<GoogleGeocodeResponse> responseOptional;

        try {
            responseOptional = googleMapsClient.buscarLugarComFiltro(textoNormalizado, null);
        } catch (RuntimeException exception) {
            logger.warn(
                    "event=localidade_google_falha_transitoria textoHash={} tentativa={} duracaoMs={} excecao={}",
                    textoHash,
                    attempt,
                    calcularDuracaoMs(inicioConsulta),
                    exception.getClass().getSimpleName()
            );
            logger.debug("Detalhes da falha transitoria durante a resolucao de localidade.", exception);
            return ResultadoConsultaGoogle.retryFalhaExterna();
        }

        long duracaoMs = calcularDuracaoMs(inicioConsulta);
        if (responseOptional.isEmpty()) {
            logger.warn(
                    "event=localidade_google_resposta_ausente textoHash={} tentativa={} duracaoMs={}",
                    textoHash,
                    attempt,
                    duracaoMs
            );
            return ResultadoConsultaGoogle.retryFalhaExterna();
        }

        GoogleGeocodeResponse response = responseOptional.get();
        String statusGoogle = normalizarStatusGoogle(response.status());

        if ("ZERO_RESULTS".equalsIgnoreCase(response.status())) {
            logger.warn(
                    "event=localidade_google_zero_results textoHash={} tentativa={} status={} quantidadeResultados={} duracaoMs={}",
                    textoHash,
                    attempt,
                    statusGoogle,
                    contarResultados(response.results()),
                    duracaoMs
            );
            return ResultadoConsultaGoogle.pendente(MOTIVO_LOCALIDADE_NAO_ENCONTRADA);
        }

        List<GoogleResult> resultados = response.results();
        if (resultados == null || resultados.isEmpty()) {
            logger.warn(
                    "event=localidade_google_resultados_vazios textoHash={} tentativa={} status={} quantidadeResultados=0 duracaoMs={}",
                    textoHash,
                    attempt,
                    statusGoogle,
                    duracaoMs
            );
            return ResultadoConsultaGoogle.pendente(MOTIVO_LOCALIDADE_NAO_ENCONTRADA);
        }

        if (!response.isSucesso()) {
            logger.warn(
                    "event=localidade_google_status_invalido textoHash={} tentativa={} status={} quantidadeResultados={} duracaoMs={}",
                    textoHash,
                    attempt,
                    statusGoogle,
                    resultados.size(),
                    duracaoMs
            );
            return ResultadoConsultaGoogle.pendente(MOTIVO_FALHA_INTEGRACAO_EXTERNA);
        }

        if (resultados.size() > 1) {
            logger.warn(
                    "event=localidade_google_resultado_ambiguo textoHash={} tentativa={} status={} quantidadeResultados={} estrategia=PENDENCIA duracaoMs={}",
                    textoHash,
                    attempt,
                    statusGoogle,
                    resultados.size(),
                    duracaoMs
            );
            return ResultadoConsultaGoogle.pendente(MOTIVO_LOCALIDADE_AMBIGUA_GOOGLE);
        }

        ResultadoConversaoGoogle resultadoConversao = converterResultadoGoogle(resultados.getFirst());
        if (resultadoConversao.localidade().isPresent()) {
            Localidade localidade = resultadoConversao.localidade().get();
            logger.info(
                    "event=localidade_google_sucesso textoHash={} tentativa={} nivel={} quantidadeResultados={} duracaoMs={}",
                    textoHash,
                    attempt,
                    determinarNivel(localidade),
                    resultados.size(),
                    duracaoMs
            );
            return ResultadoConsultaGoogle.resolvida(localidade);
        }

        logger.warn(
                "event=localidade_google_resultado_invalido textoHash={} tentativa={} quantidadeResultados={} motivo={} duracaoMs={}",
                textoHash,
                attempt,
                resultados.size(),
                resultadoConversao.motivoInvalidez(),
                duracaoMs
        );
        return ResultadoConsultaGoogle.pendente(MOTIVO_LOCALIDADE_NAO_ENCONTRADA);
    }

    private boolean aguardarRetry(String textoHash, int attempt, int maxAttempts) {
        if (googleMapsRetryDelayMs <= 0) {
            return true;
        }

        try {
            Thread.sleep(googleMapsRetryDelayMs);
            return true;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            logger.warn(
                    "event=localidade_google_retry_interrompido textoHash={} tentativaAtual={} maxTentativas={} delayMs={}",
                    textoHash,
                    attempt,
                    maxAttempts,
                    googleMapsRetryDelayMs
            );
            return false;
        }
    }

    private ResultadoConversaoGoogle converterResultadoGoogle(GoogleResult resultadoGoogle) {
        AddressComponent componentePais = extrairComponente(resultadoGoogle, "country");
        if (!componenteValido(componentePais)) {
            return ResultadoConversaoGoogle.invalida("PAIS_AUSENTE");
        }
        if (!textoPreenchido(componentePais.shortName())) {
            return ResultadoConversaoGoogle.invalida("SIGLA_PAIS_AUSENTE");
        }

        Pais pais = obterOuCriarPais(componentePais);
        AddressComponent componenteEstado = extrairComponente(resultadoGoogle, "administrative_area_level_1");
        AddressComponent componenteCidade = extrairComponente(
                resultadoGoogle,
                "locality",
                "administrative_area_level_2"
        );

        if (componenteCidade != null && !componenteValido(componenteEstado)) {
            return ResultadoConversaoGoogle.invalida("CIDADE_SEM_ESTADO");
        }

        if (!componenteValido(componenteEstado)) {
            return ResultadoConversaoGoogle.resolvida(new Localidade(pais));
        }

        Estado estado = obterOuCriarEstado(componenteEstado, pais);
        if (!componenteValido(componenteCidade)) {
            return ResultadoConversaoGoogle.resolvida(new Localidade(pais, estado));
        }

        Cidade cidade = obterOuCriarCidade(componenteCidade, estado);
        return ResultadoConversaoGoogle.resolvida(new Localidade(pais, estado, cidade));
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
            throw new BusinessRuleException("O texto da localidade \u00E9 obrigat\u00F3rio.");
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

    private String determinarNivel(Localidade localidade) {
        if (localidade.getCidade() != null) {
            return "CIDADE";
        }
        if (localidade.getEstado() != null) {
            return "ESTADO";
        }
        return "PAIS";
    }

    private String categorizarMotivoPendencia(String motivoPendencia) {
        if (MOTIVO_FALHA_INTEGRACAO_EXTERNA.equals(motivoPendencia)) {
            return "FALHA_INTEGRACAO_EXTERNA";
        }
        if (MOTIVO_LOCALIDADE_NAO_ENCONTRADA.equals(motivoPendencia)) {
            return "LOCALIDADE_NAO_ENCONTRADA";
        }
        if (MOTIVO_LOCALIDADE_AMBIGUA_BASE_INTERNA.equals(motivoPendencia)) {
            return "AMBIGUIDADE_BASE_INTERNA";
        }
        if (MOTIVO_LOCALIDADE_AMBIGUA_GOOGLE.equals(motivoPendencia)) {
            return "AMBIGUIDADE_GOOGLE_MAPS";
        }
        return "DESCONHECIDO";
    }

    private String normalizarStatusGoogle(String statusGoogle) {
        return textoPreenchido(statusGoogle) ? statusGoogle : "SEM_STATUS";
    }

    private int contarResultados(List<GoogleResult> resultados) {
        return resultados == null ? 0 : resultados.size();
    }

    private long calcularDuracaoMs(long inicioNanos) {
        return (System.nanoTime() - inicioNanos) / 1_000_000;
    }

    private String gerarTextoHash(String textoNormalizado) {
        return Long.toHexString(Integer.toUnsignedLong(textoNormalizado.hashCode()));
    }

    private record ResultadoTentativaExterna(
            Optional<Localidade> localidade,
            String motivoPendencia,
            int tentativasExecutadas
    ) {

        private static ResultadoTentativaExterna resolvida(Localidade localidade, int tentativasExecutadas) {
            return new ResultadoTentativaExterna(Optional.of(localidade), null, tentativasExecutadas);
        }

        private static ResultadoTentativaExterna pendente(String motivoPendencia, int tentativasExecutadas) {
            return new ResultadoTentativaExterna(Optional.empty(), motivoPendencia, tentativasExecutadas);
        }
    }

    private record ResultadoConsultaInterna(Optional<Localidade> localidade, String motivoPendencia) {

        private static ResultadoConsultaInterna resolvida(Localidade localidade) {
            return new ResultadoConsultaInterna(Optional.of(localidade), null);
        }

        private static ResultadoConsultaInterna ambigua() {
            return new ResultadoConsultaInterna(Optional.empty(), MOTIVO_LOCALIDADE_AMBIGUA_BASE_INTERNA);
        }

        private static ResultadoConsultaInterna semMatch() {
            return new ResultadoConsultaInterna(Optional.empty(), null);
        }
    }

    private record ResultadoConsultaGoogle(
            Optional<Localidade> localidade,
            String motivoPendencia,
            boolean deveTentarNovamente
    ) {

        private ResultadoTentativaExterna toResultadoTentativaExterna(int tentativasExecutadas) {
            return localidade.map(valor -> ResultadoTentativaExterna.resolvida(valor, tentativasExecutadas))
                    .orElseGet(() -> ResultadoTentativaExterna.pendente(motivoPendencia, tentativasExecutadas));
        }

        private static ResultadoConsultaGoogle resolvida(Localidade localidade) {
            return new ResultadoConsultaGoogle(Optional.of(localidade), null, false);
        }

        private static ResultadoConsultaGoogle pendente(String motivoPendencia) {
            return new ResultadoConsultaGoogle(Optional.empty(), motivoPendencia, false);
        }

        private static ResultadoConsultaGoogle retryFalhaExterna() {
            return new ResultadoConsultaGoogle(Optional.empty(), MOTIVO_FALHA_INTEGRACAO_EXTERNA, true);
        }
    }

    private record ResultadoConversaoGoogle(Optional<Localidade> localidade, String motivoInvalidez) {

        private static ResultadoConversaoGoogle resolvida(Localidade localidade) {
            return new ResultadoConversaoGoogle(Optional.of(localidade), null);
        }

        private static ResultadoConversaoGoogle invalida(String motivoInvalidez) {
            return new ResultadoConversaoGoogle(Optional.empty(), motivoInvalidez);
        }
    }
}
