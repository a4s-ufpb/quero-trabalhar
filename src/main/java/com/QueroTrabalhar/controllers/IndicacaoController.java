package com.QueroTrabalhar.controllers;


import com.QueroTrabalhar.domain.entity.Indicacao;
import com.QueroTrabalhar.services.IndicacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/indicacoes")
public class IndicacaoController {

    @Autowired
    private IndicacaoService indicacaoService;

    @GetMapping
    public List<Indicacao> listarIndicacoes(){
        return indicacaoService.listarIndicacoes();
    }

    @PostMapping
    public Indicacao salvarIndicacoe(@RequestBody Indicacao indicacao){
        return indicacaoService.salvarIndicacoes(indicacao);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarIndicacoe(@PathVariable Long id){
        indicacaoService.deletarIndicacoes(id);
        return ResponseEntity.noContent().build();
    }

}
