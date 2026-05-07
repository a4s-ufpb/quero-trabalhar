package com.QueroTrabalhar.services;

import com.QueroTrabalhar.domain.dtos.localidade.CidadeResponseDTO;
import com.QueroTrabalhar.domain.dtos.localidade.EstadoResponseDTO;
import com.QueroTrabalhar.domain.dtos.localidade.PaisResponseDTO;
import com.QueroTrabalhar.domain.entity.localidade.Estado;
import com.QueroTrabalhar.domain.entity.localidade.Pais;
import com.QueroTrabalhar.repository.CidadeRepository;
import com.QueroTrabalhar.repository.EstadoRepository;
import com.QueroTrabalhar.repository.PaisRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LocalidadeService {

    private final PaisRepository paisRepository;
    private final EstadoRepository estadoRepository;
    private final CidadeRepository cidadeRepository;

    public LocalidadeService(PaisRepository paisRepository,
                             EstadoRepository estadoRepository,
                             CidadeRepository cidadeRepository) {
        this.paisRepository = paisRepository;
        this.estadoRepository = estadoRepository;
        this.cidadeRepository = cidadeRepository;
    }

    @Transactional(readOnly = true)
    public List<PaisResponseDTO> buscarPais(String termoBusca) {
        List<PaisResponseDTO> paises = paisRepository.findByNomeContainingIgnoreCase(termoBusca)
                .stream()
                .map(PaisResponseDTO::daEntidade)
                .toList();

        return paises;
    }

    @Transactional(readOnly = true)
    public List<EstadoResponseDTO> buscarEstado(Long paisId, String termoBusca) {
        Pais pais = paisRepository.findById(paisId)
                .orElseThrow(() -> new IllegalArgumentException("País não encontrado."));

        // Catalogo oficial: consultas publicas usam apenas localidades ja validadas internamente.
        return estadoRepository.findByPaisAndNomeContainingIgnoreCase(pais, termoBusca)
                .stream()
                .map(EstadoResponseDTO::daEntidade)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CidadeResponseDTO> buscarCidade(Long estadoId, String termoBusca) {
        Estado estado = estadoRepository.findById(estadoId)
                .orElseThrow(() -> new IllegalArgumentException("Estado não encontrado."));

        // Catalogo oficial: Google Maps fica restrito ao fluxo de resolucao, nao ao catalogo publico.
        return cidadeRepository.findByEstadoAndNomeContainingIgnoreCase(estado, termoBusca)
                .stream()
                .map(CidadeResponseDTO::daEntidade)
                .toList();
    }
}
