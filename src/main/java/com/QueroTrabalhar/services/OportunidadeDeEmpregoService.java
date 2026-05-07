package com.QueroTrabalhar.services;

import com.QueroTrabalhar.domain.dtos.oportunidadeDeEmprego.OportunidadeDeEmpregoFilterDTO;
import com.QueroTrabalhar.domain.dtos.oportunidadeDeEmprego.OportunidadeDeEmpregoPublicaResponseDTO;
import com.QueroTrabalhar.domain.dtos.oportunidadeDeEmprego.OportunidadeDeEmpregoRequestDTO;
import com.QueroTrabalhar.domain.dtos.oportunidadeDeEmprego.OportunidadeDeEmpregoResponseDTO;
import com.QueroTrabalhar.domain.dtos.oportunidadeDeEmprego.OportunidadeRecrutadorMeFilterDTO;
import com.QueroTrabalhar.domain.entity.Empresa;
import com.QueroTrabalhar.domain.entity.OportunidadeDeEmprego;
import com.QueroTrabalhar.domain.entity.PerfilRecrutador;
import com.QueroTrabalhar.domain.entity.TipoDeEmprego;
import com.QueroTrabalhar.domain.entity.localidade.Cidade;
import com.QueroTrabalhar.domain.entity.localidade.Estado;
import com.QueroTrabalhar.domain.entity.localidade.Localidade;
import com.QueroTrabalhar.domain.entity.localidade.LocalidadePendente;
import com.QueroTrabalhar.domain.entity.localidade.Pais;
import com.QueroTrabalhar.domain.enums.CampoLocalidadePendente;
import com.QueroTrabalhar.domain.enums.Modalidade;
import com.QueroTrabalhar.domain.enums.StatusVinculoEmpresa;
import com.QueroTrabalhar.domain.enums.TipoRecursoLocalidadePendente;
import com.QueroTrabalhar.repository.CidadeRepository;
import com.QueroTrabalhar.repository.EstadoRepository;
import com.QueroTrabalhar.repository.LocalidadePendenteRepository;
import com.QueroTrabalhar.repository.OportunidadeDeEmpregoRepository;
import com.QueroTrabalhar.repository.PaisRepository;
import com.QueroTrabalhar.repository.TipoDeEmpregoRepository;
import com.QueroTrabalhar.repository.specification.OportunidadeDeEmpregoSpecification;
import com.QueroTrabalhar.repository.specification.OportunidadeRecrutadorMeSpecification;
import com.QueroTrabalhar.services.exceptions.BusinessRuleException;
import com.QueroTrabalhar.services.exceptions.ObjectNotFoundException;
import com.QueroTrabalhar.services.localidade.LocalidadeResolucaoService;
import com.QueroTrabalhar.services.localidade.RegistroLocalidadePendenteService;
import com.QueroTrabalhar.services.localidade.ResultadoResolucaoLocalidade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OportunidadeDeEmpregoService {

    private final OportunidadeDeEmpregoRepository oportunidadeDeEmpregoRepository;
    private final TipoDeEmpregoRepository tipoDeEmpregoRepository;
    private final PaisRepository paisRepository;
    private final EstadoRepository estadoRepository;
    private final CidadeRepository cidadeRepository;
    private final LocalidadePendenteRepository localidadePendenteRepository;
    private final LocalidadeResolucaoService localidadeResolucaoService;
    private final RegistroLocalidadePendenteService registroLocalidadePendenteService;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public OportunidadeDeEmpregoService(
            OportunidadeDeEmpregoRepository oportunidadeDeEmpregoRepository,
            TipoDeEmpregoRepository tipoDeEmpregoRepository,
            PaisRepository paisRepository,
            EstadoRepository estadoRepository,
            CidadeRepository cidadeRepository,
            LocalidadePendenteRepository localidadePendenteRepository,
            LocalidadeResolucaoService localidadeResolucaoService,
            RegistroLocalidadePendenteService registroLocalidadePendenteService,
            UsuarioAutenticadoService usuarioAutenticadoService
    ) {
        this.oportunidadeDeEmpregoRepository = oportunidadeDeEmpregoRepository;
        this.tipoDeEmpregoRepository = tipoDeEmpregoRepository;
        this.paisRepository = paisRepository;
        this.estadoRepository = estadoRepository;
        this.cidadeRepository = cidadeRepository;
        this.localidadePendenteRepository = localidadePendenteRepository;
        this.localidadeResolucaoService = localidadeResolucaoService;
        this.registroLocalidadePendenteService = registroLocalidadePendenteService;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    @Transactional(readOnly = true)
    public Page<OportunidadeDeEmpregoPublicaResponseDTO> listarOportunidadesDeEmprego(
            OportunidadeDeEmpregoFilterDTO filtro,
            Pageable pageable
    ) {
        return oportunidadeDeEmpregoRepository.findAll(
                OportunidadeDeEmpregoSpecification.comFiltros(filtro),
                pageable
        ).map(OportunidadeDeEmpregoPublicaResponseDTO::daEntidade);
    }

    @Transactional(readOnly = true)
    public OportunidadeDeEmpregoPublicaResponseDTO buscarPorId(Long id) {
        return OportunidadeDeEmpregoPublicaResponseDTO.daEntidade(buscarEntidadePublicaDisponivelPorId(id));
    }

    @Transactional(readOnly = true)
    public Page<OportunidadeDeEmpregoResponseDTO> listarOportunidadesDoRecrutadorAutenticado(
            OportunidadeRecrutadorMeFilterDTO filtro,
            Pageable pageable
    ) {
        PerfilRecrutador perfilRecrutador = obterPerfilRecrutadorAutenticado();
        Page<OportunidadeDeEmprego> oportunidades = oportunidadeDeEmpregoRepository.findAll(
                OportunidadeRecrutadorMeSpecification.comFiltros(perfilRecrutador.getId(), filtro),
                pageable
        );
        Map<Long, LocalidadePendente> pendenciasPorOportunidade =
                mapearPendenciasPorOportunidade(oportunidades.getContent());

        return oportunidades.map(oportunidade -> OportunidadeDeEmpregoResponseDTO.daEntidade(
                        oportunidade,
                        pendenciasPorOportunidade.get(oportunidade.getId())
                ));
    }

    @Transactional
    public OportunidadeDeEmpregoResponseDTO criarOportunidadeDeEmprego(OportunidadeDeEmpregoRequestDTO dto) {
        PerfilRecrutador perfilRecrutador = obterPerfilRecrutadorAutenticado();
        TipoDeEmprego tipoDeEmprego = buscarTipoDeEmpregoValido(dto.tipoDeEmpregoId());
        Empresa empresaDaOportunidade = resolverEmpresaDaOportunidade(
                perfilRecrutador,
                dto.publicarComoEmpresa()
        );

        OportunidadeDeEmprego oportunidadeDeEmprego = new OportunidadeDeEmprego(
                dto.descricao(),
                tipoDeEmprego,
                dto.modalidade(),
                null,
                perfilRecrutador,
                empresaDaOportunidade
        );
        LocalidadePendente localidadePendente = definirLocalidadeDaOportunidade(oportunidadeDeEmprego, dto);

        perfilRecrutador.adicionarOportunidadePostada(oportunidadeDeEmprego);

        OportunidadeDeEmprego oportunidadeSalva = oportunidadeDeEmpregoRepository.save(oportunidadeDeEmprego);
        associarDonoGenericoDaPendenciaSeNecessario(localidadePendente, oportunidadeSalva.getId());

        return OportunidadeDeEmpregoResponseDTO.daEntidade(
                oportunidadeSalva,
                buscarPendenciaLocalidadeDaOportunidade(oportunidadeSalva)
        );
    }

    @Transactional
    public OportunidadeDeEmpregoResponseDTO atualizarOportunidadeDeEmprego(
            Long id,
            OportunidadeDeEmpregoRequestDTO dto
    ) {
        PerfilRecrutador perfilRecrutador = obterPerfilRecrutadorAutenticado();
        OportunidadeDeEmprego oportunidadeDeEmprego = buscarEntidadePorId(id);

        validarDonoDaOportunidade(oportunidadeDeEmprego, perfilRecrutador);

        // Nesta fase, o contexto original de publicacao da oportunidade e preservado.
        oportunidadeDeEmprego.setDescricao(dto.descricao());
        oportunidadeDeEmprego.setTipoDeEmprego(buscarTipoDeEmpregoValido(dto.tipoDeEmpregoId()));
        oportunidadeDeEmprego.setModalidade(dto.modalidade());
        LocalidadePendente localidadePendente = definirLocalidadeDaOportunidade(oportunidadeDeEmprego, dto);

        OportunidadeDeEmprego oportunidadeSalva = oportunidadeDeEmpregoRepository.save(oportunidadeDeEmprego);
        associarDonoGenericoDaPendenciaSeNecessario(localidadePendente, oportunidadeSalva.getId());

        return OportunidadeDeEmpregoResponseDTO.daEntidade(
                oportunidadeSalva,
                buscarPendenciaLocalidadeDaOportunidade(oportunidadeSalva)
        );
    }

    @Transactional
    public void removerOportunidadeDeEmprego(Long id) {
        PerfilRecrutador perfilRecrutador = obterPerfilRecrutadorAutenticado();
        OportunidadeDeEmprego oportunidadeDeEmprego = buscarEntidadePorId(id);

        validarDonoDaOportunidade(oportunidadeDeEmprego, perfilRecrutador);

        oportunidadeDeEmpregoRepository.removerTodosInteressesDaVaga(id);
        oportunidadeDeEmpregoRepository.delete(oportunidadeDeEmprego);
    }

    private OportunidadeDeEmprego buscarEntidadePorId(Long id) {
        return oportunidadeDeEmpregoRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Oportunidade de emprego nao encontrada. ID: " + id));
    }

    private OportunidadeDeEmprego buscarEntidadePublicaPorId(Long id) {
        return oportunidadeDeEmpregoRepository.findByIdAndLocalidadePaisIsNotNull(id)
                .orElseThrow(() -> new ObjectNotFoundException("Oportunidade de emprego nao encontrada. ID: " + id));
    }

    private OportunidadeDeEmprego buscarEntidadePublicaDisponivelPorId(Long id) {
        return oportunidadeDeEmpregoRepository.findByIdAndLocalidadePaisIsNotNull(id)
                .orElseThrow(() -> new ObjectNotFoundException("Oportunidade de emprego nao encontrada. ID: " + id));
    }

    PerfilRecrutador obterPerfilRecrutadorAutenticado() {
        return usuarioAutenticadoService.obterPerfilRecrutadorAutenticado();
    }

    private TipoDeEmprego buscarTipoDeEmpregoValido(Long tipoDeEmpregoId) {
        TipoDeEmprego tipoDeEmprego = tipoDeEmpregoRepository.findById(tipoDeEmpregoId)
                .orElseThrow(() -> new ObjectNotFoundException(
                        "Tipo de emprego nao encontrado. ID: " + tipoDeEmpregoId
                ));

        if (!tipoDeEmprego.isAprovado()) {
            throw new BusinessRuleException(
                    "O tipo de emprego informado ainda nao foi aprovado e nao pode ser usado em oportunidades."
            );
        }

        return tipoDeEmprego;
    }

    private LocalidadePendente definirLocalidadeDaOportunidade(
            OportunidadeDeEmprego oportunidadeDeEmprego,
            OportunidadeDeEmpregoRequestDTO dto
    ) {
        if (possuiLocalidadeEstruturadaPorIds(dto)) {
            oportunidadeDeEmprego.definirLocalidadeValidada(
                    montarLocalidade(dto.paisId(), dto.estadoId(), dto.cidadeId())
            );
            return null;
        }

        String localidadeTexto = normalizarCampoOpcional(dto.localidadeTexto());
        if (localidadeTexto != null) {
            return aplicarResultadoResolucaoLocalidade(
                    oportunidadeDeEmprego,
                    localidadeResolucaoService.resolver(localidadeTexto)
            );
        }

        throw new BusinessRuleException("A localidade da oportunidade e obrigatoria.");
    }

    private LocalidadePendente aplicarResultadoResolucaoLocalidade(
            OportunidadeDeEmprego oportunidadeDeEmprego,
            ResultadoResolucaoLocalidade resultadoResolucao
    ) {
        if (resultadoResolucao.resolvida()) {
            oportunidadeDeEmprego.definirLocalidadeValidada(resultadoResolucao.localidadeValidada());
            return null;
        }

        limparLocalidadeValidada(oportunidadeDeEmprego);
        return resultadoResolucao.localidadePendente();
    }

    private void limparLocalidadeValidada(OportunidadeDeEmprego oportunidadeDeEmprego) {
        oportunidadeDeEmprego.setLocalizacao(null);
    }

    private void associarDonoGenericoDaPendenciaSeNecessario(
            LocalidadePendente localidadePendente,
            Long oportunidadeId
    ) {
        if (localidadePendente == null) {
            return;
        }

        registroLocalidadePendenteService.associarDonoGenerico(
                localidadePendente,
                TipoRecursoLocalidadePendente.OPORTUNIDADE_DE_EMPREGO,
                oportunidadeId,
                CampoLocalidadePendente.LOCALIDADE
        );
    }

    private LocalidadePendente buscarPendenciaLocalidadeDaOportunidade(OportunidadeDeEmprego oportunidadeDeEmprego) {
        if (oportunidadeDeEmprego.getLocalizacao() != null || oportunidadeDeEmprego.getId() == null) {
            return null;
        }

        return localidadePendenteRepository.findFirstByTipoRecursoAndRecursoIdAndCampoAlvoOrderByAtualizadaEmDescCriadaEmDesc(
                TipoRecursoLocalidadePendente.OPORTUNIDADE_DE_EMPREGO,
                oportunidadeDeEmprego.getId(),
                CampoLocalidadePendente.LOCALIDADE
        ).orElse(null);
    }

    private Map<Long, LocalidadePendente> mapearPendenciasPorOportunidade(List<OportunidadeDeEmprego> oportunidades) {
        List<Long> oportunidadeIds = oportunidades.stream()
                .filter(oportunidade -> oportunidade.getLocalizacao() == null)
                .map(OportunidadeDeEmprego::getId)
                .filter(id -> id != null)
                .distinct()
                .toList();

        if (oportunidadeIds.isEmpty()) {
            return Map.of();
        }

        Map<Long, LocalidadePendente> pendenciasPorOportunidade = new HashMap<>();
        localidadePendenteRepository.findByTipoRecursoAndCampoAlvoAndRecursoIdInOrderByRecursoIdAscAtualizadaEmDescCriadaEmDesc(
                TipoRecursoLocalidadePendente.OPORTUNIDADE_DE_EMPREGO,
                CampoLocalidadePendente.LOCALIDADE,
                oportunidadeIds
        ).forEach(pendencia -> pendenciasPorOportunidade.putIfAbsent(
                pendencia.getRecursoId(),
                pendencia
        ));

        return pendenciasPorOportunidade;
    }

    private boolean possuiLocalidadeEstruturadaPorIds(OportunidadeDeEmpregoRequestDTO dto) {
        return dto.paisId() != null || dto.estadoId() != null || dto.cidadeId() != null;
    }

    private Localidade montarLocalidade(Long paisId, Long estadoId, Long cidadeId) {
        if (cidadeId != null && estadoId == null) {
            throw new BusinessRuleException("Para informar uma cidade, o estado tambem deve ser informado.");
        }

        if (paisId == null) {
            throw new BusinessRuleException("O pais e obrigatorio quando a localidade for informada por IDs.");
        }

        Pais pais = paisRepository.findById(paisId)
                .orElseThrow(() -> new ObjectNotFoundException("Pais nao encontrado. ID: " + paisId));

        if (estadoId == null) {
            return new Localidade(pais);
        }

        Estado estado = estadoRepository.findById(estadoId)
                .orElseThrow(() -> new ObjectNotFoundException("Estado nao encontrado. ID: " + estadoId));

        if (!estado.getPais().getId().equals(pais.getId())) {
            throw new BusinessRuleException("O estado informado nao pertence ao pais informado.");
        }

        if (cidadeId == null) {
            return new Localidade(pais, estado);
        }

        Cidade cidade = cidadeRepository.findById(cidadeId)
                .orElseThrow(() -> new ObjectNotFoundException("Cidade nao encontrada. ID: " + cidadeId));

        if (!cidade.getEstado().getId().equals(estado.getId())) {
            throw new BusinessRuleException("A cidade informada nao pertence ao estado informado.");
        }

        return new Localidade(pais, estado, cidade);
    }

    private String normalizarCampoOpcional(String valor) {
        if (valor == null) {
            return null;
        }

        String valorNormalizado = valor.trim();
        return valorNormalizado.isEmpty() ? null : valorNormalizado;
    }

    private Empresa resolverEmpresaDaOportunidade(
            PerfilRecrutador perfilRecrutador,
            Boolean publicarComoEmpresa
    ) {
        if (!Boolean.TRUE.equals(publicarComoEmpresa)) {
            return null;
        }

        if (perfilRecrutador.getEmpresaVinculada() == null) {
            throw new BusinessRuleException(
                    "Para publicar uma oportunidade em nome da empresa, o recrutador autenticado precisa possuir empresa vinculada."
            );
        }

        if (perfilRecrutador.getStatusVinculoEmpresa() != StatusVinculoEmpresa.APROVADO) {
            throw new BusinessRuleException(
                    "Para publicar uma oportunidade em nome da empresa, o vinculo com a empresa precisa estar aprovado."
            );
        }

        return perfilRecrutador.getEmpresaVinculada();
    }

    private void validarDonoDaOportunidade(
            OportunidadeDeEmprego oportunidadeDeEmprego,
            PerfilRecrutador perfilRecrutadorAutenticado
    ) {
        if (!oportunidadeDeEmprego.getPerfilRecrutador().getId().equals(perfilRecrutadorAutenticado.getId())) {
            throw new BusinessRuleException(
                    "O recrutador autenticado nao pode alterar uma oportunidade que pertence a outro perfil."
            );
        }
    }
}
