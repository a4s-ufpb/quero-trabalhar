package com.QueroTrabalhar.controllers;

import com.QueroTrabalhar.domain.dtos.oportunidadeDeEmprego.OportunidadeDeEmpregoRequestDTO;
import com.QueroTrabalhar.domain.dtos.oportunidadeDeEmprego.OportunidadeDeEmpregoResponseDTO;
import com.QueroTrabalhar.services.OportunidadeDeEmpregoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/oportunidades")
public class OportunidadeDeEmpregoController {

    private final OportunidadeDeEmpregoService oportunidadeDeEmpregoService;

    public OportunidadeDeEmpregoController(OportunidadeDeEmpregoService oportunidadeDeEmpregoService) {
        this.oportunidadeDeEmpregoService = oportunidadeDeEmpregoService;
    }

    @GetMapping
    public ResponseEntity<List<OportunidadeDeEmpregoResponseDTO>> listar() {
        return ResponseEntity.ok(oportunidadeDeEmpregoService.listarTodasOportunidadesDeEmprego());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OportunidadeDeEmpregoResponseDTO> buscarOportunidadeDeEmpregoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(oportunidadeDeEmpregoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<OportunidadeDeEmpregoResponseDTO> salvarOportunidadeDeEmprego(
            @RequestBody @Valid OportunidadeDeEmpregoRequestDTO oportunidadeDeEmpregoRequestDTO
    ) {
        OportunidadeDeEmpregoResponseDTO oportunidadeCriada =
                oportunidadeDeEmpregoService.criarOportunidadeDeEmprego(oportunidadeDeEmpregoRequestDTO);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(oportunidadeCriada.id())
                .toUri();

        return ResponseEntity.created(uri).body(oportunidadeCriada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OportunidadeDeEmpregoResponseDTO> atualizarOportunidadeDeEmprego(
            @PathVariable Long id,
            @RequestBody @Valid OportunidadeDeEmpregoRequestDTO oportunidadeDeEmpregoRequestDTO
    ) {
        return ResponseEntity.ok(
                oportunidadeDeEmpregoService.atualizarOportunidadeDeEmprego(id, oportunidadeDeEmpregoRequestDTO)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerOportunidadeDeEmprego(@PathVariable Long id) {
        oportunidadeDeEmpregoService.removerOportunidadeDeEmprego(id);
        return ResponseEntity.noContent().build();
    }
}
