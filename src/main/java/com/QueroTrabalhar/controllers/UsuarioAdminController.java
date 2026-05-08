package com.QueroTrabalhar.controllers;

import com.QueroTrabalhar.domain.dtos.perfilRecrutador.PerfilRecrutadorUsuarioRequestDTO;
import com.QueroTrabalhar.domain.dtos.usuario.UsuarioAtualizacaoRequestDTO;
import com.QueroTrabalhar.domain.dtos.usuario.UsuarioFilterDTO;
import com.QueroTrabalhar.domain.dtos.usuario.UsuarioResponseDTO;
import com.QueroTrabalhar.services.UsuarioService;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioAdminController {

    private final UsuarioService usuarioService;

    public UsuarioAdminController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<Page<UsuarioResponseDTO>> listarTodos(
            @Valid @ParameterObject UsuarioFilterDTO filtro,
            @ParameterObject
            @PageableDefault(page = 0, size = 10, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(usuarioService.listarUsuarios(filtro, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarUsuarioPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizarUsuario(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioAtualizacaoRequestDTO usuarioRequest
    ) {
        return ResponseEntity.ok(usuarioService.atualizarUsuarioComoAdmin(id, usuarioRequest));
    }

    @DeleteMapping("/{id}/perfil-candidato")
    public ResponseEntity<Void> removerPerfilCandidatoPorId(@PathVariable Long id) {
        usuarioService.removerPerfilCandidatoPorId(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/perfil-recrutador")
    public ResponseEntity<Void> removerPerfilRecrutadorPorId(@PathVariable Long id) {
        usuarioService.removerPerfilRecrutadorPorId(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/perfil-candidato")
    public ResponseEntity<Void> adicionarPerfilCandidato(@PathVariable Long id) {
        usuarioService.adicionarPerfilCandidatoPorId(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/perfil-recrutador")
    public ResponseEntity<Void> adicionarPerfilRecrutador(
            @PathVariable Long id,
            @Valid @RequestBody PerfilRecrutadorUsuarioRequestDTO perfilRecrutadorRequest
    ) {
        usuarioService.adicionarPerfilRecrutadorPorId(id, perfilRecrutadorRequest);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerUsuarioPorId(@PathVariable Long id) {
        usuarioService.deletarUsuarioPorId(id);
        return ResponseEntity.noContent().build();
    }
}
