package com.QueroTrabalhar.services;

import com.QueroTrabalhar.domain.dtos.empresa.EmpresaRequestDTO;
import com.QueroTrabalhar.domain.dtos.empresa.EmpresaResponseDTO;
import com.QueroTrabalhar.domain.entity.Empresa;
import com.QueroTrabalhar.domain.entity.localidade.Cidade;
import com.QueroTrabalhar.domain.entity.localidade.Estado;
import com.QueroTrabalhar.domain.entity.localidade.Localidade;
import com.QueroTrabalhar.domain.entity.localidade.Pais;
import com.QueroTrabalhar.repository.CidadeRepository;
import com.QueroTrabalhar.repository.EmpresaRepository;
import com.QueroTrabalhar.repository.EstadoRepository;
import com.QueroTrabalhar.repository.PaisRepository;
import com.QueroTrabalhar.services.exceptions.BusinessRuleException;
import com.QueroTrabalhar.services.exceptions.ObjectNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final PaisRepository paisRepository;
    private final EstadoRepository estadoRepository;
    private final CidadeRepository cidadeRepository;

    public EmpresaService(
            EmpresaRepository empresaRepository,
            PaisRepository paisRepository,
            EstadoRepository estadoRepository,
            CidadeRepository cidadeRepository
    ) {
        this.empresaRepository = empresaRepository;
        this.paisRepository = paisRepository;
        this.estadoRepository = estadoRepository;
        this.cidadeRepository = cidadeRepository;
    }

    @Transactional
    public EmpresaResponseDTO criarEmpresa(EmpresaRequestDTO dto) {
        Localidade localidade = montarLocalidade(dto.paisId(), dto.estadoId(), dto.cidadeId());

        Empresa empresa = new Empresa(
                dto.nome().trim(),
                normalizarCampoOpcional(dto.descricao()),
                normalizarCampoOpcional(dto.site()),
                normalizarCampoOpcional(dto.emailPublico()),
                normalizarCampoOpcional(dto.telefonePublico()),
                localidade
        );

        return EmpresaResponseDTO.daEntidade(empresaRepository.save(empresa));
    }

    @Transactional(readOnly = true)
    public List<EmpresaResponseDTO> listarEmpresas() {
        return empresaRepository.findAllByOrderByNomeAsc().stream()
                .map(EmpresaResponseDTO::daEntidade)
                .toList();
    }

    @Transactional(readOnly = true)
    public EmpresaResponseDTO buscarEmpresaPorId(Long id) {
        return EmpresaResponseDTO.daEntidade(buscarEntidadePorId(id));
    }

    private Empresa buscarEntidadePorId(Long id) {
        return empresaRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Empresa nao encontrada. ID: " + id));
    }

    private Localidade montarLocalidade(Long paisId, Long estadoId, Long cidadeId) {
        if (cidadeId != null && estadoId == null) {
            throw new BusinessRuleException("Para informar uma cidade, o estado tambem deve ser informado.");
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
