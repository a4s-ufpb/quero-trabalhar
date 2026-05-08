package com.QueroTrabalhar.controllers;

import com.QueroTrabalhar.domain.dtos.oportunidadeDeEmprego.OportunidadeInteresseCandidatoFilterDTO;
import com.QueroTrabalhar.domain.dtos.oportunidadeDeEmprego.OportunidadeDeEmpregoResponseDTO;
import com.QueroTrabalhar.services.PerfilCandidatoService;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<Page<OportunidadeDeEmpregoResponseDTO>> listarMinhasVagasDeInteresse(
            @Valid @ParameterObject OportunidadeInteresseCandidatoFilterDTO filtro,
            @ParameterObject
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(candidatoService.listarMinhasVagasDeInteresse(filtro, pageable));
    }
}
