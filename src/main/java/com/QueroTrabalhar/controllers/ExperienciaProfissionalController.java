package com.QueroTrabalhar.controllers;

import com.QueroTrabalhar.domain.dtos.experienciaProfissional.ExperienciaProfissionalRequestDTO;
import com.QueroTrabalhar.domain.dtos.experienciaProfissional.ExperienciaProfissionalResponseDTO;
import com.QueroTrabalhar.services.ExperienciaProfissionalService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios/me/perfil-candidato/experiencias")
public class ExperienciaProfissionalController {

    private final ExperienciaProfissionalService experienciaProfissionalService;

    public ExperienciaProfissionalController(
            ExperienciaProfissionalService experienciaProfissionalService
    ) {
        this.experienciaProfissionalService = experienciaProfissionalService;
    }

    @GetMapping
    public ResponseEntity<List<ExperienciaProfissionalResponseDTO>> listarExperienciasProfissionais() {
        return ResponseEntity.ok(experienciaProfissionalService.listarTodasAsExperienciaProfissionais());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExperienciaProfissionalResponseDTO> buscarMinhaExperienciaProfissionalPorId(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(experienciaProfissionalService.buscarMinhaExperienciaPorId(id));
    }

    @PostMapping
    public ResponseEntity<ExperienciaProfissionalResponseDTO> cadastrarExperienciaProfissional(
            @Valid @RequestBody ExperienciaProfissionalRequestDTO experienciaProfissional
    ) {
        ExperienciaProfissionalResponseDTO experienciaCriada =
                experienciaProfissionalService.adicionarMinhaExperienciaProfissional(experienciaProfissional);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(experienciaCriada.id())
                .toUri();

        return ResponseEntity.created(uri).body(experienciaCriada);
    }

    @DeleteMapping("/{experienciaId}")
    public ResponseEntity<Void> deletarMinhaExperienciaProfissional(@PathVariable Long experienciaId) {
        experienciaProfissionalService.deletarMinhaExperiencia(experienciaId);
        return ResponseEntity.noContent().build();
    }
}
