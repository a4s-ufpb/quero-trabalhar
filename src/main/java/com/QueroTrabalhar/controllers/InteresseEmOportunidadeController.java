package com.QueroTrabalhar.controllers;

import com.QueroTrabalhar.entity.InteresseEmOportunidades;
import com.QueroTrabalhar.services.InteresseEmOportunidadesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController // Indica que esta classe é um controlador REST
@RequestMapping("api/interesse-em-oportunidade") // Define a URL base para endpoints deste controlador
public class InteresseEmOportunidadeController {

    @Autowired
    private InteresseEmOportunidadesService interesseEmOportunidadesService;

    @GetMapping
    public List<InteresseEmOportunidades> listarTodasOsInteressesDeEmprego(){
        return interesseEmOportunidadesService.listarTodasOsInteressesDeEmprego();
    }

    @GetMapping ("/{id}")
    public ResponseEntity<InteresseEmOportunidades> buscarInteressesPorID(@PathVariable Long id) {
        Optional<InteresseEmOportunidades> interesseEmEmprego = interesseEmOportunidadesService.buscarInteresseEmEmpregoPorID(id);
        return interesseEmEmprego.map( ResponseEntity::ok ).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public InteresseEmOportunidades salvarInteresseDeEmprego(@RequestBody InteresseEmOportunidades interesseEmOportunidades) {
        return interesseEmOportunidadesService.salvarInteresseEmEmprego(interesseEmOportunidades);
    }

    @DeleteMapping
    public ResponseEntity<Void> deletarInteresseEmEmprego(@PathVariable Long id) {
        interesseEmOportunidadesService.deletarInteresseEmEmprego(id);
        return ResponseEntity.noContent().build();
    }
}
