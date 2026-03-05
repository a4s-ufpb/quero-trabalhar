package com.QueroTrabalhar.services;

import com.QueroTrabalhar.domain.entity.OportunidadeDeEmprego;
import com.QueroTrabalhar.domain.entity.PerfilCandidato;
import com.QueroTrabalhar.repository.OportunidadeDeEmpregoRepository;
import com.QueroTrabalhar.repository.PerfilCandidatoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PerfilCandidatoService {

    @Autowired
    private PerfilCandidatoRepository candidatoRepository;

    @Autowired
    private OportunidadeDeEmpregoRepository oportunidadeRepository;

    // --- GESTÃO DE INTERESSES EM VAGAS ---

    @Transactional
    public void demonstrarInteresseEmVaga(Long candidatoId, Long oportunidadeId) {
        PerfilCandidato candidato = candidatoRepository.findById(candidatoId)
                .orElseThrow(() -> new EntityNotFoundException("Candidato não encontrado. ID: " + candidatoId));

        OportunidadeDeEmprego vaga = oportunidadeRepository.findById(oportunidadeId)
                .orElseThrow(() -> new EntityNotFoundException("Vaga não encontrada. ID: " + oportunidadeId));

        if (!candidato.getVagasDeInteresse().contains(vaga)) {
            candidato.demonstrarInteresse(vaga);
            candidatoRepository.save(candidato);
        } else {
            throw new IllegalArgumentException("Você já demonstrou interesse nesta vaga.");
        }
    }

    @Transactional
    public void removerInteresseEmVaga(Long candidatoId, Long oportunidadeId) {
        PerfilCandidato candidato = candidatoRepository.findById(candidatoId)
                .orElseThrow(() -> new EntityNotFoundException("Candidato não encontrado. ID: " + candidatoId));

        OportunidadeDeEmprego vaga = oportunidadeRepository.findById(oportunidadeId)
                .orElseThrow(() -> new EntityNotFoundException("Vaga não encontrada. ID: " + oportunidadeId));

        if (candidato.getVagasDeInteresse().contains(vaga)) {
            candidato.removerInteresse(vaga);
            candidatoRepository.save(candidato);
        } else {
            throw new IllegalArgumentException("Esta vaga não está na sua lista de interesses.");
        }
    }

    // Nota: Aqui futuramente você adicionará o método de adicionar/remover ExperienciaProfissional
}