package com.QueroTrabalhar.controllers;

import com.QueroTrabalhar.domain.dtos.experienciaProfissional.ExperienciaProfissionalRequestDTO;
import com.QueroTrabalhar.domain.dtos.experienciaProfissional.ExperienciaProfissionalResponseDTO;
import com.QueroTrabalhar.services.ExperienciaProfissionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/candidatos/{perfilCandidatoId}/experiencias")
public class ExperienciaProfissionalController {

    @Autowired
    private ExperienciaProfissionalService experienciaProfissionalService;

    @GetMapping
    public ResponseEntity<List<ExperienciaProfissionalResponseDTO>> listarExperienciasProfissionais() {
        return ResponseEntity.ok().body(experienciaProfissionalService.listarTodasAsExperienciaProfissionais());
    }

    @GetMapping ("/{id}")
    public ResponseEntity<ExperienciaProfissionalResponseDTO> buscarExperienciaProfissionalPorId(@PathVariable Long id) {
        return ResponseEntity.ok().body(experienciaProfissionalService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<ExperienciaProfissionalResponseDTO> cadastrarExperienciaProfissional
            (@PathVariable Long perfilCandidatoId, @RequestBody ExperienciaProfissionalRequestDTO experienciaProfissional) {
        ExperienciaProfissionalResponseDTO expEmprego = experienciaProfissionalService.criarExperienciaProfissional(perfilCandidatoId, experienciaProfissional);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(
                        expEmprego.id()
                ).toUri();

        return ResponseEntity.created(uri).body(expEmprego);
    }

    @DeleteMapping("/{experienciaId}")
    public ResponseEntity<Void> deletarExperienciaProfissional(@PathVariable Long perfilCandidatoId, @PathVariable Long experienciaId){
            experienciaProfissionalService.deletarExperiencia(perfilCandidatoId, experienciaId);
            return ResponseEntity.noContent().build();
    }
}
