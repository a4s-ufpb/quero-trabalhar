package com.QueroTrabalhar.controllers;

import com.QueroTrabalhar.domain.dtos.oportunidadeDeEmprego.OportunidadeDeEmpregoFilterDTO;
import com.QueroTrabalhar.domain.dtos.oportunidadeDeEmprego.OportunidadeDeEmpregoPublicaResponseDTO;
import com.QueroTrabalhar.domain.dtos.oportunidadeDeEmprego.OportunidadeDeEmpregoRequestDTO;
import com.QueroTrabalhar.domain.dtos.oportunidadeDeEmprego.OportunidadeDeEmpregoResponseDTO;
import com.QueroTrabalhar.services.OportunidadeDeEmpregoService;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Sort;
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

@RestController
@RequestMapping("/api/oportunidades")
public class OportunidadeDeEmpregoController {

    private final OportunidadeDeEmpregoService oportunidadeDeEmpregoService;

    public OportunidadeDeEmpregoController(OportunidadeDeEmpregoService oportunidadeDeEmpregoService) {
        this.oportunidadeDeEmpregoService = oportunidadeDeEmpregoService;
    }

    @GetMapping
    public ResponseEntity<Page<OportunidadeDeEmpregoPublicaResponseDTO>> listar(
            @Valid @ParameterObject OportunidadeDeEmpregoFilterDTO filtro,
            @ParameterObject
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(oportunidadeDeEmpregoService.listarOportunidadesDeEmprego(filtro, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OportunidadeDeEmpregoPublicaResponseDTO> buscarOportunidadeDeEmpregoPorId(
            @PathVariable Long id
    ) {
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
