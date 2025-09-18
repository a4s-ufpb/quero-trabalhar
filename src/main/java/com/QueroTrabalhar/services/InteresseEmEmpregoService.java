package com.QueroTrabalhar.services;

import com.QueroTrabalhar.entity.InteresseEmEmprego;
import com.QueroTrabalhar.repository.InteresseEmEmpregoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InteresseEmEmpregoService {

    @Autowired
    private InteresseEmEmpregoRepository interesseEmEmpregoRepository;

    public List<InteresseEmEmprego> ListarInteressesEmEmpregos() {
        return interesseEmEmpregoRepository.findAll();
    }

    public InteresseEmEmprego salvarInteresseEmEmprego(InteresseEmEmprego interesseEmEmprego) {
        return interesseEmEmpregoRepository.save(interesseEmEmprego);
    }

    public void deletarInteresseEmEmprego(Long id) {
        interesseEmEmpregoRepository.deleteById(id);
    }
}
