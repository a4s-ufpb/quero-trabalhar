package com.QueroTrabalhar.controllers;

import com.QueroTrabalhar.domain.dtos.oportunidadeDeEmprego.OportunidadeDeEmpregoResponseDTO;
import com.QueroTrabalhar.services.PerfilCandidatoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/candidatos")
public class PerfilCandidatoController {

    private final PerfilCandidatoService candidatoService;

    public PerfilCandidatoController(PerfilCandidatoService candidatoService) {
        this.candidatoService = candidatoService;
    }

    @PostMapping("/me/interesses/vagas/{vagaId}")
    public ResponseEntity<Void> demonstrarInteresse(@PathVariable Long vagaId) {
        candidatoService.demonstrarInteresseEmVaga(vagaId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me/interesses/vagas/{vagaId}")
    public ResponseEntity<Void> removerInteresse(@PathVariable Long vagaId) {
        candidatoService.removerInteresseEmVaga(vagaId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/interesses/vagas")
    public ResponseEntity<List<OportunidadeDeEmpregoResponseDTO>> listarMinhasVagasDeInteresse() {
        return ResponseEntity.ok(candidatoService.listarMinhasVagasDeInteresse());
    }
}
