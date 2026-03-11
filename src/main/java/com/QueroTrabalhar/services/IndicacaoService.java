package com.QueroTrabalhar.services;


import com.QueroTrabalhar.domain.entity.Indicacao;
import com.QueroTrabalhar.repository.IndicacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IndicacaoService {

    @Autowired
    private IndicacaoRepository indicacaoRepository;


    public List<Indicacao> listarIndicacoes(){
        return indicacaoRepository.findAll();
    }

    public Indicacao salvarIndicacoes(Indicacao indicacao){
        return indicacaoRepository.save(indicacao);
    }

    public void deletarIndicacoes(Long id){
        indicacaoRepository.deleteById(id);
    }
}
