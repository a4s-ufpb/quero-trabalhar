package com.QueroTrabalhar.controllers;

import com.QueroTrabalhar.domain.dtos.usuario.UsuarioRequestDTO;
import com.QueroTrabalhar.domain.dtos.usuario.UsuarioResponseDTO;
import com.QueroTrabalhar.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioAdminController {

    private final UsuarioService usuarioService;

    public UsuarioAdminController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarTodosUsuarios());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarUsuarioPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizarUsuario(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioRequestDTO usuarioRequest
    ) {
        return ResponseEntity.ok(usuarioService.atualizarUsuario(id, usuarioRequest));
    }

    @DeleteMapping("/{id}/perfil_candidato")
    public ResponseEntity<Void> removerPerfilCandidatoPorId(@PathVariable Long id) {
        usuarioService.removerPerfilCandidatoPorId(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/perfil_recrutador")
    public ResponseEntity<Void> removerPerfilRecrutadorPorId(@PathVariable Long id) {
        usuarioService.removerPerfilRecrutadorPorId(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/perfil_candidato")
    public ResponseEntity<Void> adicionarPerfilCandidato(@PathVariable Long id) {
        usuarioService.adicionarPerfilCandidatoPorId(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/perfil_recrutador/{nomeDaEmpresa}")
    public ResponseEntity<Void> adicionarPerfilRecrutador(
            @PathVariable Long id,
            @PathVariable String nomeDaEmpresa
    ) {
        usuarioService.adicionarPerfilRecrutadorPorId(id, nomeDaEmpresa);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerUsuarioPorId(@PathVariable Long id) {
        usuarioService.deletarUsuarioPorId(id);
        return ResponseEntity.noContent().build();
    }
}
