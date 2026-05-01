package com.QueroTrabalhar.controllers;

import com.QueroTrabalhar.domain.dtos.experienciaProfissional.ExperienciaProfissionalRequestDTO;
import com.QueroTrabalhar.domain.dtos.experienciaProfissional.ExperienciaProfissionalResponseDTO;
import com.QueroTrabalhar.services.ExperienciaProfissionalService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/experiencias")
@PreAuthorize("hasRole('ADMIN')")
public class ExperienciaProfissionalAdminController {

    private final ExperienciaProfissionalService experienciaService;

    public ExperienciaProfissionalAdminController(ExperienciaProfissionalService experienciaService) {
        this.experienciaService = experienciaService;
    }

    @GetMapping
    public ResponseEntity<List<ExperienciaProfissionalResponseDTO>> listarTodasAsExperiencias() {
        return ResponseEntity.ok(experienciaService.listarTodasExperienciasComoAdmin());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExperienciaProfissionalResponseDTO> buscarExperienciaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(experienciaService.buscarExperienciaPorIdComoAdmin(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExperienciaProfissionalResponseDTO> atualizarExperiencia(
            @PathVariable Long id,
            @Valid @RequestBody ExperienciaProfissionalRequestDTO dto
    ) {
        return ResponseEntity.ok(experienciaService.atualizarExperienciaComoAdmin(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarExperiencia(@PathVariable Long id) {
        experienciaService.deletarExperienciaComoAdmin(id);
        return ResponseEntity.noContent().build();
    }
}
