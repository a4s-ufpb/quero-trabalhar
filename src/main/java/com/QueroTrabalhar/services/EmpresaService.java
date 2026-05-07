package com.QueroTrabalhar.services;

import com.QueroTrabalhar.domain.dtos.empresa.EmpresaPublicaResponseDTO;
import com.QueroTrabalhar.domain.dtos.empresa.EmpresaRequestDTO;
import com.QueroTrabalhar.domain.dtos.empresa.EmpresaResponseDTO;
import com.QueroTrabalhar.domain.dtos.oportunidadeDeEmprego.OportunidadeDeEmpregoPublicaResponseDTO;
import com.QueroTrabalhar.domain.dtos.oportunidadeDeEmprego.OportunidadeDeEmpregoResponseDTO;
import com.QueroTrabalhar.domain.dtos.perfilRecrutador.RecrutadorDaEmpresaResponseDTO;
import com.QueroTrabalhar.domain.entity.Empresa;
import com.QueroTrabalhar.domain.entity.localidade.Cidade;
import com.QueroTrabalhar.domain.entity.localidade.Estado;
import com.QueroTrabalhar.domain.entity.localidade.Localidade;
import com.QueroTrabalhar.domain.entity.localidade.LocalidadePendente;
import com.QueroTrabalhar.domain.entity.localidade.Pais;
import com.QueroTrabalhar.domain.enums.CampoLocalidadePendente;
import com.QueroTrabalhar.domain.enums.StatusVinculoEmpresa;
import com.QueroTrabalhar.domain.enums.TipoRecursoLocalidadePendente;
import com.QueroTrabalhar.repository.CidadeRepository;
import com.QueroTrabalhar.repository.EmpresaRepository;
import com.QueroTrabalhar.repository.EstadoRepository;
import com.QueroTrabalhar.repository.LocalidadePendenteRepository;
import com.QueroTrabalhar.repository.OportunidadeDeEmpregoRepository;
import com.QueroTrabalhar.repository.PaisRepository;
import com.QueroTrabalhar.repository.PerfilRecrutadorRepository;
import com.QueroTrabalhar.services.exceptions.BusinessRuleException;
import com.QueroTrabalhar.services.exceptions.ObjectNotFoundException;
import com.QueroTrabalhar.services.localidade.LocalidadeResolucaoService;
import com.QueroTrabalhar.services.localidade.RegistroLocalidadePendenteService;
import com.QueroTrabalhar.services.localidade.ResultadoResolucaoLocalidade;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final PerfilRecrutadorRepository perfilRecrutadorRepository;
    private final OportunidadeDeEmpregoRepository oportunidadeDeEmpregoRepository;
    private final PaisRepository paisRepository;
    private final EstadoRepository estadoRepository;
    private final CidadeRepository cidadeRepository;
    private final LocalidadePendenteRepository localidadePendenteRepository;
    private final LocalidadeResolucaoService localidadeResolucaoService;
    private final RegistroLocalidadePendenteService registroLocalidadePendenteService;

    public EmpresaService(
            EmpresaRepository empresaRepository,
            PerfilRecrutadorRepository perfilRecrutadorRepository,
            OportunidadeDeEmpregoRepository oportunidadeDeEmpregoRepository,
            PaisRepository paisRepository,
            EstadoRepository estadoRepository,
            CidadeRepository cidadeRepository,
            LocalidadePendenteRepository localidadePendenteRepository,
            LocalidadeResolucaoService localidadeResolucaoService,
            RegistroLocalidadePendenteService registroLocalidadePendenteService
    ) {
        this.empresaRepository = empresaRepository;
        this.perfilRecrutadorRepository = perfilRecrutadorRepository;
        this.oportunidadeDeEmpregoRepository = oportunidadeDeEmpregoRepository;
        this.paisRepository = paisRepository;
        this.estadoRepository = estadoRepository;
        this.cidadeRepository = cidadeRepository;
        this.localidadePendenteRepository = localidadePendenteRepository;
        this.localidadeResolucaoService = localidadeResolucaoService;
        this.registroLocalidadePendenteService = registroLocalidadePendenteService;
    }

    @Transactional
    public EmpresaResponseDTO criarEmpresa(EmpresaRequestDTO dto) {
        Empresa empresa = new Empresa(
                dto.nome().trim(),
                normalizarCampoOpcional(dto.descricao()),
                normalizarCampoOpcional(dto.site()),
                normalizarCampoOpcional(dto.emailPublico()),
                normalizarCampoOpcional(dto.telefonePublico()),
                null
        );
        LocalidadePendente localidadePendente = definirLocalidadeDaEmpresa(empresa, dto);

        Empresa empresaSalva = empresaRepository.save(empresa);
        associarDonoGenericoDaPendenciaSeNecessario(localidadePendente, empresaSalva.getId());

        return EmpresaResponseDTO.daEntidade(empresaSalva, buscarPendenciaLocalidadeDaEmpresa(empresaSalva));
    }

    @Transactional(readOnly = true)
    public List<EmpresaPublicaResponseDTO> listarEmpresas() {
        return empresaRepository.findByLocalidadePaisIsNotNullOrderByNomeAsc().stream()
                .map(EmpresaPublicaResponseDTO::daEntidade)
                .toList();
    }

    @Transactional(readOnly = true)
    public EmpresaPublicaResponseDTO buscarEmpresaPorId(Long id) {
        return EmpresaPublicaResponseDTO.daEntidade(buscarEntidadePublicaDisponivelPorId(id));
    }

    @Transactional(readOnly = true)
    public List<RecrutadorDaEmpresaResponseDTO> listarRecrutadoresAprovadosDaEmpresa(Long empresaId) {
        buscarEntidadePublicaDisponivelPorId(empresaId);

        return perfilRecrutadorRepository
                .findByEmpresaVinculadaIdAndStatusVinculoEmpresa(empresaId, StatusVinculoEmpresa.APROVADO)
                .stream()
                .map(RecrutadorDaEmpresaResponseDTO::daEntidade)
                .sorted(Comparator.comparing(
                        RecrutadorDaEmpresaResponseDTO::nome,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OportunidadeDeEmpregoPublicaResponseDTO> listarOportunidadesDaEmpresa(Long empresaId) {
        buscarEntidadePublicaDisponivelPorId(empresaId);

        return oportunidadeDeEmpregoRepository.findByEmpresaIdAndLocalidadePaisIsNotNull(empresaId).stream()
                .map(OportunidadeDeEmpregoPublicaResponseDTO::daEntidade)
                .toList();
    }

    private Empresa buscarEntidadePorId(Long id) {
        return empresaRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Empresa nao encontrada. ID: " + id));
    }

    private Empresa buscarEntidadePublicaPorId(Long id) {
        return empresaRepository.findByIdAndLocalidadePaisIsNotNull(id)
                .orElseThrow(() -> new ObjectNotFoundException("Empresa nao encontrada. ID: " + id));
    }

    private Empresa buscarEntidadePublicaDisponivelPorId(Long id) {
        return empresaRepository.findByIdAndLocalidadePaisIsNotNull(id)
                .orElseThrow(() -> new ObjectNotFoundException("Empresa nao encontrada. ID: " + id));
    }

    private LocalidadePendente definirLocalidadeDaEmpresa(Empresa empresa, EmpresaRequestDTO dto) {
        if (possuiLocalidadeEstruturadaPorIds(dto)) {
            empresa.definirLocalidadeValidada(montarLocalidade(dto.paisId(), dto.estadoId(), dto.cidadeId()));
            return null;
        }

        String localidadeTexto = normalizarCampoOpcional(dto.localidadeTexto());
        if (localidadeTexto != null) {
            return aplicarResultadoResolucaoLocalidade(empresa, localidadeResolucaoService.resolver(localidadeTexto));
        }

        throw new BusinessRuleException("A localidade da empresa e obrigatoria.");
    }

    private LocalidadePendente aplicarResultadoResolucaoLocalidade(
            Empresa empresa,
            ResultadoResolucaoLocalidade resultadoResolucao
    ) {
        if (resultadoResolucao.resolvida()) {
            empresa.definirLocalidadeValidada(resultadoResolucao.localidadeValidada());
            return null;
        }

        limparEstadoLegadoDaLocalidadePendente(empresa);
        return resultadoResolucao.localidadePendente();
    }

    private void limparEstadoLegadoDaLocalidadePendente(Empresa empresa) {
        empresa.setLocalidade(null);
        empresa.setLocalidadePendente(null);
    }

    private void associarDonoGenericoDaPendenciaSeNecessario(LocalidadePendente localidadePendente, Long empresaId) {
        if (localidadePendente == null) {
            return;
        }

        registroLocalidadePendenteService.associarDonoGenerico(
                localidadePendente,
                TipoRecursoLocalidadePendente.EMPRESA,
                empresaId,
                CampoLocalidadePendente.LOCALIDADE
        );
    }

    private LocalidadePendente buscarPendenciaLocalidadeDaEmpresa(Empresa empresa) {
        if (empresa.getLocalidade() != null || empresa.getId() == null) {
            return null;
        }

        return localidadePendenteRepository.findFirstByTipoRecursoAndRecursoIdAndCampoAlvoOrderByAtualizadaEmDescCriadaEmDesc(
                TipoRecursoLocalidadePendente.EMPRESA,
                empresa.getId(),
                CampoLocalidadePendente.LOCALIDADE
        ).orElse(null);
    }

    private boolean possuiLocalidadeEstruturadaPorIds(EmpresaRequestDTO dto) {
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
}
