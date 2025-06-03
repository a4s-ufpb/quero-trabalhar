package com.QueroTrabalhar.controllers;

import com.QueroTrabalhar.entity.ExperienciaProfissional;
import com.QueroTrabalhar.services.ExperienciaProfissionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController // Indica que esta classe é um controlador REST
@RequestMapping("api/experiencia-profissional") // Define a URL base para endpoints deste controlador
public class ExperienciaProfissionalController {

    @Autowired
    private ExperienciaProfissionalService experienciaProfissionalService;

    @GetMapping
    public List<ExperienciaProfissional> listarExperienciasProfissionais() {
        return experienciaProfissionalService.listarTodasAsExperienciaProfissionais();
    }

    @GetMapping ("/{id}")
    public ResponseEntity<ExperienciaProfissional> buscarExperienciaProfissionalPorId(@PathVariable Long id) {
        Optional<ExperienciaProfissional> experienciaProfissional = experienciaProfissionalService.listarPorId(id);
        return experienciaProfissional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ExperienciaProfissional cadastrarExperienciaProfissional(@RequestBody ExperienciaProfissional experienciaProfissional) {
        return experienciaProfissionalService.cadastrarExperienciaProfissional(experienciaProfissional);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarExperienciaProfissional(@PathVariable Long id){
        experienciaProfissionalService.deletarExperienciaProfissional(id);
        return ResponseEntity.noContent().build();
    }
}
