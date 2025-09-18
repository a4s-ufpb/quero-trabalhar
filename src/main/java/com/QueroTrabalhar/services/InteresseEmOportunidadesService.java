package com.QueroTrabalhar.services;

import com.QueroTrabalhar.entity.InteresseEmOportunidades;
import com.QueroTrabalhar.repository.InteresseEmOportunidadesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InteresseEmOportunidadesService {

    @Autowired
    private InteresseEmOportunidadesRepository interesseEmOportunidadesRepository;


    public List<InteresseEmOportunidades> listarTodasOsInteressesDeEmprego() {
        return interesseEmOportunidadesRepository.findAll();
    }

    public Optional<InteresseEmOportunidades> buscarInteresseEmEmpregoPorID(Long id) {
        return interesseEmOportunidadesRepository.findById(id);
    }

    public InteresseEmOportunidades salvarInteresseEmEmprego(InteresseEmOportunidades interesse) {
        return interesseEmOportunidadesRepository.save(interesse);
    }

    public void deletarInteresseEmEmprego(Long id) {
        interesseEmOportunidadesRepository.deleteById(id);
    }
}
