package com.QueroTrabalhar.controllers;

import com.QueroTrabalhar.domain.dtos.usuario.UsuarioRequestDTO;
import com.QueroTrabalhar.domain.dtos.usuario.UsuarioResponseDTO;
import com.QueroTrabalhar.services.UsuarioService;
import jakarta.validation.Valid;
import org.aspectj.apache.bcel.generic.LocalVariableGen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() {
        List<UsuarioResponseDTO> usuarios = usuarioService.listarTodosUsuarios();
        return ResponseEntity.ok().body(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id) {
        UsuarioResponseDTO response = usuarioService.buscarUsuarioPorId(id);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<UsuarioResponseDTO> cadastrarUsuario(@RequestBody @Valid UsuarioRequestDTO usuarioRequest) {
        UsuarioResponseDTO usuarioResponse = usuarioService.cadastrarUsuario(usuarioRequest);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(
                        usuarioResponse.id()
                ).toUri();

        return ResponseEntity.created(uri).body(usuarioResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizarUsuario(@PathVariable Long id, @Valid @RequestBody UsuarioRequestDTO usuarioRequest) {
        return ResponseEntity.ok().body(usuarioService.atualizarUsuario(id, usuarioRequest));
    }

    @PutMapping("/{id}/remover_perfil_usuario")
    public ResponseEntity<Void> removerPerfilCandidato(@PathVariable Long id) {
        usuarioService.removerPerfilCandidato(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/remover_perfil_recrutador")
    public ResponseEntity<Void> removerPerfilRecrutador(@PathVariable Long id) {
        usuarioService.removerPerfilRecrutador(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/adicionar_perfil_usuario")
    public ResponseEntity<Void> adicionarPerfilCandidato(@PathVariable Long id) {
        usuarioService.adicionarPerfilCandidato(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/adicionar_perfil_recrutador")
    public ResponseEntity<Void> adicionarPerfilRecrutador(@PathVariable Long id) {
        usuarioService.adicionarPerfilRecrutador(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerUsuario(@PathVariable Long id) {
        usuarioService.deletarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}