package com.QueroTrabalhar.services;

import com.QueroTrabalhar.entity.InteresseEmEmprego;
import com.QueroTrabalhar.repository.InteresseEmEmpregoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InteresseEmEmpregoService {

    @Autowired
    private InteresseEmEmpregoRepository interesseEmEmpregoRepository;


    public List<InteresseEmEmprego> listarTodasOsInteressesDeEmprego() {
        return interesseEmEmpregoRepository.findAll();
    }

    public Optional<InteresseEmEmprego> buscarInteresseEmEmpregoPorID(Long id) {
        return interesseEmEmpregoRepository.findById(id);
    }

    public InteresseEmEmprego salvarInteresseEmEmprego(InteresseEmEmprego interesse) {
        return interesseEmEmpregoRepository.save(interesse);
    }

    public void deletarInteresseEmEmprego(Long id) {
        interesseEmEmpregoRepository.deleteById(id);
    }
}
