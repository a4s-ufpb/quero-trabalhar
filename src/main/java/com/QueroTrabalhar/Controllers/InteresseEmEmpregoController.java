package com.QueroTrabalhar.Controllers;

import com.QueroTrabalhar.Entity.InteresseEmEmprego;
import com.QueroTrabalhar.Services.InteresseEmEmpregoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController // Indica que esta classe é um controlador REST
@RequestMapping("api/interesse-em-emprego") // Define a URL base para endpoints deste controlador
public class InteresseEmEmpregoController {

    @Autowired
    private InteresseEmEmpregoService interesseEmEmpregoService;

    @GetMapping
    public List<InteresseEmEmprego> listarTodasOsInteressesDeEmprego(){
        return interesseEmEmpregoService.listarTodasOsInteressesDeEmprego();
    }

    @GetMapping ("/{id}")
    public ResponseEntity<InteresseEmEmprego> buscarInteressesPorID(@PathVariable Long id) {
        Optional<InteresseEmEmprego> interesseEmEmprego = interesseEmEmpregoService.buscarInteresseEmEmpregoPorID(id);
        return interesseEmEmprego.map( ResponseEntity::ok ).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public InteresseEmEmprego salvarInteresseDeEmprego(@RequestBody InteresseEmEmprego interesseEmEmprego) {
        return interesseEmEmpregoService.salvarInteresseEmEmprego(interesseEmEmprego);
    }

    @DeleteMapping
    public ResponseEntity<Void> deletarInteresseEmEmprego(@PathVariable Long id) {
        interesseEmEmpregoService.deletarInteresseEmEmprego(id);
        return ResponseEntity.noContent().build();
    }
}
