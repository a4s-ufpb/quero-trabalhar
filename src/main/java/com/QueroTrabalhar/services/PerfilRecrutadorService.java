package com.QueroTrabalhar.services;

import com.QueroTrabalhar.domain.entity.OportunidadeDeEmprego;
import com.QueroTrabalhar.domain.entity.PerfilRecrutador;
import com.QueroTrabalhar.repository.OportunidadeDeEmpregoRepository;
import com.QueroTrabalhar.repository.PerfilRecrutadorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PerfilRecrutadorService {

    @Autowired
    private PerfilRecrutadorRepository recrutadorRepository;

    @Autowired
    private OportunidadeDeEmpregoRepository oportunidadeRepository;

    @Transactional
    public OportunidadeDeEmprego postarNovaVaga(Long recrutadorId, OportunidadeDeEmprego novaVaga) {
        PerfilRecrutador recrutador = recrutadorRepository.findById(recrutadorId)
                .orElseThrow(() -> new EntityNotFoundException("Recrutador não encontrado. ID: " + recrutadorId));

        recrutador.adicionarOportunidadePostada(novaVaga);

        // Salvando o recrutador, a vaga é salva junto pelo CascadeType.ALL
        recrutadorRepository.save(recrutador);

        return novaVaga;
    }
}