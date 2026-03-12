package com.QueroTrabalhar.controllers;

import com.QueroTrabalhar.domain.dtos.tipoDeEmprego.TipoDeEmpregoRequestDTO;
import com.QueroTrabalhar.domain.dtos.tipoDeEmprego.TipoDeEmpregoResponseDTO;
import com.QueroTrabalhar.services.TipoDeEmpregoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tipos-de-emprego")
public class TipoDeEmpregoController {

    @Autowired
    private TipoDeEmpregoService tipoDeEmpregoService;

    @GetMapping("/aprovados")
    public ResponseEntity<List<TipoDeEmpregoResponseDTO>> listarAprovados() {
        return ResponseEntity.ok().body(tipoDeEmpregoService.listarAprovados());
    }

    //Usado por admins
    @GetMapping("/nao-aprovados")
    public ResponseEntity<List<TipoDeEmpregoResponseDTO>> listarNaoAprovados() {
        return ResponseEntity.ok().body(tipoDeEmpregoService.listarNaoAprovados());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoDeEmpregoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok().body(tipoDeEmpregoService.buscarPorId(id));
    }

    //Usado por admins
    @PostMapping("/criar")
    public ResponseEntity<TipoDeEmpregoResponseDTO> criarNoCatalogo(@Valid @RequestBody TipoDeEmpregoRequestDTO tipoDeEmprego) {
        TipoDeEmpregoResponseDTO tipoDeEmpregoCriado = tipoDeEmpregoService.criarNoCatalogo(tipoDeEmprego);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(
                        tipoDeEmpregoCriado.id()
                ).toUri();
        return ResponseEntity.created(uri).body(tipoDeEmpregoCriado);
    }

    @PostMapping("/sugerir")
    public ResponseEntity<TipoDeEmpregoResponseDTO> sugerirNoCatalogo(@Valid @RequestBody TipoDeEmpregoRequestDTO tipoDeEmprego) {
        TipoDeEmpregoResponseDTO tipoDeEmpregoSugerido = tipoDeEmpregoService.sugerirNoCatalogo(tipoDeEmprego);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(
                        tipoDeEmpregoSugerido.id()
                ).toUri();

        return ResponseEntity.created(uri).body(tipoDeEmpregoSugerido);
    }

    //Usado por admins
    //Pode ser que tenha uma melhor forma de fazer isso, mas acredito que seja uma decisão entre back e front
    @PutMapping("/{id}/aprovar")
    public ResponseEntity<TipoDeEmpregoResponseDTO> aprovarSugestao(@PathVariable Long id,@Valid @RequestBody TipoDeEmpregoRequestDTO tipoDeEmprego) {
        TipoDeEmpregoResponseDTO aprovado = tipoDeEmpregoService.aprovarSugestao(id, tipoDeEmprego.titulo(), tipoDeEmprego.descricao());
        return ResponseEntity.ok().body(aprovado);
    }

    //Usado por admins
    @PatchMapping("/aprovar-lote")
    public ResponseEntity<Void> aprovarEmLote(@RequestBody List<Long> ids) {
        tipoDeEmpregoService.aprovarEmLote(ids);
        return ResponseEntity.noContent().build();
    }

    //Usado por admins
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        tipoDeEmpregoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}