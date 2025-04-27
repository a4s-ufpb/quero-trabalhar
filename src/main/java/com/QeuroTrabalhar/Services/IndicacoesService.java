package com.QeuroTrabalhar.Services;


import com.QeuroTrabalhar.Entity.Indicacoes;
import com.QeuroTrabalhar.Repository.IndicacoesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IndicacoesService {

    @Autowired
    private IndicacoesRepository indicacoesRepository;


    public List<Indicacoes> listarIndicacoes(){
        return indicacoesRepository.findAll();
    }

    public Indicacoes salvarIndicacoes(Indicacoes indicacoes){
        return indicacoesRepository.save(indicacoes);
    }

    public void deletarIndicacoes(Long id){
        indicacoesRepository.deleteById(id);
    }
}
