package com.QueroTrabalhar.controllers;

import com.QueroTrabalhar.services.PerfilCandidatoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/candidatos")
public class PerfilCandidatoController {

    @Autowired
    private PerfilCandidatoService candidatoService;

    // POST /api/candidatos/1/interesses/vagas/5
    @PostMapping("/{candidatoId}/interesses/vagas/{vagaId}")
    public ResponseEntity<String> demonstrarInteresse(@PathVariable Long candidatoId, @PathVariable Long vagaId) {
        candidatoService.demonstrarInteresseEmVaga(candidatoId, vagaId);
        return ResponseEntity.ok("Interesse registrado com sucesso!");
    }

    // DELETE /api/candidatos/1/interesses/vagas/5
    @DeleteMapping("/{candidatoId}/interesses/vagas/{vagaId}")
    public ResponseEntity<String> removerInteresse(@PathVariable Long candidatoId, @PathVariable Long vagaId) {
        candidatoService.removerInteresseEmVaga(candidatoId, vagaId);
        return ResponseEntity.ok("Interesse removido com sucesso!");
    }
}