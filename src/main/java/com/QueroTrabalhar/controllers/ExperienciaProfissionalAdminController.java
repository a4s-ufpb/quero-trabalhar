package com.QueroTrabalhar.controllers;

import com.QueroTrabalhar.domain.dtos.experienciaProfissional.ExperienciaProfissionalRequestDTO;
import com.QueroTrabalhar.domain.dtos.experienciaProfissional.ExperienciaProfissionalResponseDTO;
import com.QueroTrabalhar.services.ExperienciaProfissionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/experiencias")
// @PreAuthorize("hasRole('ADMIN')") // Descomente quando for religar a segurança!
public class ExperienciaProfissionalAdminController {

    @Autowired
    private ExperienciaProfissionalService experienciaService;

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
            @RequestBody ExperienciaProfissionalRequestDTO dto) {
        return ResponseEntity.ok(experienciaService.atualizarExperienciaComoAdmin(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarExperiencia(@PathVariable Long id) {
        experienciaService.deletarExperienciaComoAdmin(id);
        return ResponseEntity.noContent().build();
    }
}