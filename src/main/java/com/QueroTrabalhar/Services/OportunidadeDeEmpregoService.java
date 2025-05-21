package com.QueroTrabalhar.Services;

import com.QueroTrabalhar.Entity.OportunidadeDeEmprego;
import com.QueroTrabalhar.Repository.OportunidadeDeEmpregoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OportunidadeDeEmpregoService {

    @Autowired
    private OportunidadeDeEmpregoRepository oportunidadeDeEmpregoRepository;


    public List<OportunidadeDeEmprego> listarTodasOportunidadeDeEmprego() {
        return oportunidadeDeEmpregoRepository.findAll();
    }

    public Optional<OportunidadeDeEmprego> buscarPorId(Long id) {
        return oportunidadeDeEmpregoRepository.findById(id);
    }

    public OportunidadeDeEmprego salvarOportunidadeDeEmprego (OportunidadeDeEmprego oportunidadeDeEmprego) {
        return oportunidadeDeEmpregoRepository.save(oportunidadeDeEmprego);
    }

    public void removerOportunidadeDeEmprego(Long id) {
        oportunidadeDeEmpregoRepository.deleteById(id);
    }
}
