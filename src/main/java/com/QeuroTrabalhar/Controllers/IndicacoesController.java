package com.QeuroTrabalhar.Controllers;


import com.QeuroTrabalhar.Entity.Indicacoes;
import com.QeuroTrabalhar.Services.IndicacoesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/indicacoes")
public class IndicacoesController {

    @Autowired
    private IndicacoesService indicacoesService;

    @GetMapping
    public List<Indicacoes> listarIndicacoes(){
        return indicacoesService.listarIndicacoes();
    }

    @PostMapping
    public Indicacoes salvarIndicacoe(@RequestBody Indicacoes indicacoes){
        return indicacoesService.salvarIndicacoes(indicacoes);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarIndicacoe(@PathVariable Long id){
        indicacoesService.deletarIndicacoes(id);
        return ResponseEntity.noContent().build();
    }

}
