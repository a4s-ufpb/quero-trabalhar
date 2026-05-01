package com.QueroTrabalhar.controllers;

import com.QueroTrabalhar.domain.dtos.tipoDeEmprego.TipoDeEmpregoRequestDTO;
import com.QueroTrabalhar.domain.dtos.tipoDeEmprego.TipoDeEmpregoResponseDTO;
import com.QueroTrabalhar.services.TipoDeEmpregoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/tipos-de-emprego")
public class TipoDeEmpregoController {

    private final TipoDeEmpregoService tipoDeEmpregoService;

    public TipoDeEmpregoController(TipoDeEmpregoService tipoDeEmpregoService) {
        this.tipoDeEmpregoService = tipoDeEmpregoService;
    }

    @GetMapping("/aprovados")
    public ResponseEntity<List<TipoDeEmpregoResponseDTO>> listarAprovados() {
        return ResponseEntity.ok(tipoDeEmpregoService.listarAprovados());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoDeEmpregoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(tipoDeEmpregoService.buscarAprovadoPorId(id));
    }

    @PostMapping("/sugerir")
    public ResponseEntity<TipoDeEmpregoResponseDTO> sugerirNoCatalogo(
            @Valid @RequestBody TipoDeEmpregoRequestDTO tipoDeEmprego
    ) {
        TipoDeEmpregoResponseDTO tipoDeEmpregoSugerido = tipoDeEmpregoService.sugerirNoCatalogo(tipoDeEmprego);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(tipoDeEmpregoSugerido.id())
                .toUri();

        return ResponseEntity.created(uri).body(tipoDeEmpregoSugerido);
    }
}
