package com.QueroTrabalhar.controllers;

import com.QueroTrabalhar.domain.entity.TipoDeEmprego;
import com.QueroTrabalhar.services.TipoDeEmpregoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tipos-de-emprego") // Adicionei a barra inicial (padrão REST)
public class TipoDeEmpregoController {

    @Autowired
    private TipoDeEmpregoService tipoDeEmpregoService;

    // --- ENDPOINTS PÚBLICOS (Para uso no Front-end geral) ---

    // 1. Listar apenas os cargos APROVADOS (Dropdown de cadastro)
    @GetMapping
    public List<TipoDeEmprego> listarTodos() {
        // CORREÇÃO: Chama o novo método do Service
        return tipoDeEmpregoService.listarTiposAprovados();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoDeEmprego> buscarPorId(@PathVariable Long id) {
        Optional<TipoDeEmprego> tipoDeEmprego = tipoDeEmpregoService.buscarPorId(id);
        return tipoDeEmprego.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // --- ENDPOINTS ADMINISTRATIVOS (Para você ou o Professor) ---

    // 2. Listar sugestões pendentes enviadas pelos usuários
    @GetMapping("/pendentes")
    public List<TipoDeEmprego> listarPendentes() {
        return tipoDeEmpregoService.listarSugestoesPendentes();
    }

    // 3. Criar um cargo oficial direto (Ignora o fluxo de sugestão)
    @PostMapping
    public ResponseEntity<TipoDeEmprego> criar(@RequestBody TipoDeEmprego tipoDeEmprego) {
        // CORREÇÃO: Chama o método salvarOficial
        TipoDeEmprego salvo = tipoDeEmpregoService.salvarOficial(tipoDeEmprego);
        return ResponseEntity.status(201).body(salvo);
    }

    // 4. Aprovar uma sugestão de usuário e torná-la oficial
    @PutMapping("/{id}/aprovar")
    public ResponseEntity<TipoDeEmprego> aprovarSugestao(
            @PathVariable Long id,
            @RequestParam String tituloCorrigido,
            @RequestParam String descricao) {

        TipoDeEmprego aprovado = tipoDeEmpregoService.aprovarSugestao(id, tituloCorrigido, descricao);
        return ResponseEntity.ok(aprovado);
    }

    // 5. Deletar (Serve tanto para cargos oficiais quanto para recusar sugestões)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        tipoDeEmpregoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}