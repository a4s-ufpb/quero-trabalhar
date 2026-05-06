package com.QueroTrabalhar.services;

import com.QueroTrabalhar.domain.dtos.empresa.EmpresaRequestDTO;
import com.QueroTrabalhar.domain.dtos.empresa.EmpresaResponseDTO;
import com.QueroTrabalhar.domain.dtos.oportunidadeDeEmprego.OportunidadeDeEmpregoResponseDTO;
import com.QueroTrabalhar.domain.dtos.perfilRecrutador.RecrutadorDaEmpresaResponseDTO;
import com.QueroTrabalhar.domain.entity.Empresa;
import com.QueroTrabalhar.domain.entity.localidade.Cidade;
import com.QueroTrabalhar.domain.entity.localidade.Estado;
import com.QueroTrabalhar.domain.entity.localidade.Localidade;
import com.QueroTrabalhar.domain.entity.localidade.Pais;
import com.QueroTrabalhar.domain.enums.StatusVinculoEmpresa;
import com.QueroTrabalhar.repository.CidadeRepository;
import com.QueroTrabalhar.repository.EmpresaRepository;
import com.QueroTrabalhar.repository.EstadoRepository;
import com.QueroTrabalhar.repository.OportunidadeDeEmpregoRepository;
import com.QueroTrabalhar.repository.PaisRepository;
import com.QueroTrabalhar.repository.PerfilRecrutadorRepository;
import com.QueroTrabalhar.services.exceptions.BusinessRuleException;
import com.QueroTrabalhar.services.exceptions.ObjectNotFoundException;
import com.QueroTrabalhar.services.localidade.LocalidadeResolucaoService;
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
    private final LocalidadeResolucaoService localidadeResolucaoService;

    public EmpresaService(
            EmpresaRepository empresaRepository,
            PerfilRecrutadorRepository perfilRecrutadorRepository,
            OportunidadeDeEmpregoRepository oportunidadeDeEmpregoRepository,
            PaisRepository paisRepository,
            EstadoRepository estadoRepository,
            CidadeRepository cidadeRepository,
            LocalidadeResolucaoService localidadeResolucaoService
    ) {
        this.empresaRepository = empresaRepository;
        this.perfilRecrutadorRepository = perfilRecrutadorRepository;
        this.oportunidadeDeEmpregoRepository = oportunidadeDeEmpregoRepository;
        this.paisRepository = paisRepository;
        this.estadoRepository = estadoRepository;
        this.cidadeRepository = cidadeRepository;
        this.localidadeResolucaoService = localidadeResolucaoService;
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
        definirLocalidadeDaEmpresa(empresa, dto);

        return EmpresaResponseDTO.daEntidade(empresaRepository.save(empresa));
    }

    @Transactional(readOnly = true)
    public List<EmpresaResponseDTO> listarEmpresas() {
        return empresaRepository.findByLocalidadePaisIsNotNullOrderByNomeAsc().stream()
                .map(EmpresaResponseDTO::daEntidade)
                .toList();
    }

    @Transactional(readOnly = true)
    public EmpresaResponseDTO buscarEmpresaPorId(Long id) {
        return EmpresaResponseDTO.daEntidade(buscarEntidadePorId(id));
    }

    @Transactional(readOnly = true)
    public List<RecrutadorDaEmpresaResponseDTO> listarRecrutadoresAprovadosDaEmpresa(Long empresaId) {
        buscarEntidadePorId(empresaId);

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
    public List<OportunidadeDeEmpregoResponseDTO> listarOportunidadesDaEmpresa(Long empresaId) {
        buscarEntidadePorId(empresaId);

        return oportunidadeDeEmpregoRepository.findByEmpresaId(empresaId).stream()
                .map(OportunidadeDeEmpregoResponseDTO::daEntidade)
                .toList();
    }

    private Empresa buscarEntidadePorId(Long id) {
        return empresaRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Empresa não encontrada. ID: " + id));
    }

    private void definirLocalidadeDaEmpresa(Empresa empresa, EmpresaRequestDTO dto) {
        if (possuiLocalidadeEstruturadaPorIds(dto)) {
            empresa.definirLocalidadeValidada(montarLocalidade(dto.paisId(), dto.estadoId(), dto.cidadeId()));
            return;
        }

        String localidadeTexto = normalizarCampoOpcional(dto.localidadeTexto());
        if (localidadeTexto != null) {
            ResultadoResolucaoLocalidade resultadoResolucao = localidadeResolucaoService.resolver(localidadeTexto);
            if (resultadoResolucao.resolvida()) {
                empresa.definirLocalidadeValidada(resultadoResolucao.localidadeValidada());
                return;
            }

            empresa.definirLocalidadePendente(resultadoResolucao.localidadePendente());
            return;
        }

        throw new BusinessRuleException("A localidade da empresa é obrigatória.");
    }

    private boolean possuiLocalidadeEstruturadaPorIds(EmpresaRequestDTO dto) {
        return dto.paisId() != null || dto.estadoId() != null || dto.cidadeId() != null;
    }

    private Localidade montarLocalidade(Long paisId, Long estadoId, Long cidadeId) {
        if (cidadeId != null && estadoId == null) {
            throw new BusinessRuleException("Para informar uma cidade, o estado também deve ser informado.");
        }

        if (paisId == null) {
            throw new BusinessRuleException("O país é obrigatório quando a localidade for informada por IDs.");
        }

        Pais pais = paisRepository.findById(paisId)
                .orElseThrow(() -> new ObjectNotFoundException("País não encontrado. ID: " + paisId));

        if (estadoId == null) {
            return new Localidade(pais);
        }

        Estado estado = estadoRepository.findById(estadoId)
                .orElseThrow(() -> new ObjectNotFoundException("Estado não encontrado. ID: " + estadoId));

        if (!estado.getPais().getId().equals(pais.getId())) {
            throw new BusinessRuleException("O estado informado não pertence ao país informado.");
        }

        if (cidadeId == null) {
            return new Localidade(pais, estado);
        }

        Cidade cidade = cidadeRepository.findById(cidadeId)
                .orElseThrow(() -> new ObjectNotFoundException("Cidade não encontrada. ID: " + cidadeId));

        if (!cidade.getEstado().getId().equals(estado.getId())) {
            throw new BusinessRuleException("A cidade informada não pertence ao estado informado.");
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
