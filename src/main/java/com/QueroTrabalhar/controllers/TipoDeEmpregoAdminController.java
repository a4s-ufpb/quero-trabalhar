package com.QueroTrabalhar.controllers;

import com.QueroTrabalhar.domain.dtos.tipoDeEmprego.TipoDeEmpregoRequestDTO;
import com.QueroTrabalhar.domain.dtos.tipoDeEmprego.TipoDeEmpregoResponseDTO;
import com.QueroTrabalhar.services.TipoDeEmpregoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/admin/tipos-emprego")
@PreAuthorize("hasRole('ADMIN')")
public class TipoDeEmpregoAdminController {

    private final TipoDeEmpregoService tipoDeEmpregoService;

    public TipoDeEmpregoAdminController(TipoDeEmpregoService tipoDeEmpregoService) {
        this.tipoDeEmpregoService = tipoDeEmpregoService;
    }

    @GetMapping("/nao-aprovados")
    public ResponseEntity<List<TipoDeEmpregoResponseDTO>> listarNaoAprovados() {
        return ResponseEntity.ok(tipoDeEmpregoService.listarNaoAprovados());
    }

    @PostMapping
    public ResponseEntity<TipoDeEmpregoResponseDTO> criarNoCatalogo(
            @Valid @RequestBody TipoDeEmpregoRequestDTO tipoDeEmprego
    ) {
        TipoDeEmpregoResponseDTO tipoDeEmpregoCriado = tipoDeEmpregoService.criarNoCatalogo(tipoDeEmprego);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(tipoDeEmpregoCriado.id())
                .toUri();

        return ResponseEntity.created(uri).body(tipoDeEmpregoCriado);
    }

    @PatchMapping("/{id}/aprovar")
    public ResponseEntity<TipoDeEmpregoResponseDTO> aprovarSugestao(
            @PathVariable Long id,
            @Valid @RequestBody TipoDeEmpregoRequestDTO tipoDeEmprego
    ) {
        TipoDeEmpregoResponseDTO aprovado = tipoDeEmpregoService.aprovarSugestao(
                id,
                tipoDeEmprego.titulo(),
                tipoDeEmprego.descricao()
        );
        return ResponseEntity.ok(aprovado);
    }

    @PatchMapping("/aprovar-lote")
    public ResponseEntity<Void> aprovarEmLote(@RequestBody List<Long> ids) {
        tipoDeEmpregoService.aprovarEmLote(ids);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        tipoDeEmpregoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
