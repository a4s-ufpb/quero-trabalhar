package com.QueroTrabalhar.controllers;

import com.QueroTrabalhar.domain.entity.OportunidadeDeEmprego;
import com.QueroTrabalhar.services.PerfilRecrutadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recrutadores")
public class PerfilRecrutadorController {

    @Autowired
    private PerfilRecrutadorService recrutadorService;

    // POST /api/recrutadores/2/vagas
    @PostMapping("/{recrutadorId}/vagas")
    public ResponseEntity<String> postarVaga(
            @PathVariable Long recrutadorId,
            @RequestBody OportunidadeDeEmprego vagaRequisicao) { // Futuramente troque por um Record (VagaDTORequest)

        recrutadorService.postarNovaVaga(recrutadorId, vagaRequisicao);
        return ResponseEntity.status(201).body("Vaga publicada com sucesso!");
    }
}