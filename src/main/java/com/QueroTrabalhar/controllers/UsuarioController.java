package com.QueroTrabalhar.controllers;

import com.QueroTrabalhar.domain.dtos.usuario.UsuarioRequestDTO;
import com.QueroTrabalhar.domain.dtos.usuario.UsuarioResponseDTO;
import com.QueroTrabalhar.services.UsuarioService;
import jakarta.validation.Valid;
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

    @DeleteMapping("/me/perfil-candidato")
    public ResponseEntity<Void> removerMeuPerfilCandidato() {
        usuarioService.removerMeuPerfilCandidato();
        return ResponseEntity.noContent().build();
    }

    //Usado por admins/superadmin vou configurar certinho depois
    //@PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    @DeleteMapping("/{id}/perfil_candidato")
    public ResponseEntity<Void> removerPerfilCandidatoPorId(@PathVariable Long id) {
        usuarioService.removerPerfilCandidatoPorId(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me/perfil-recrutador")
    public ResponseEntity<Void> removerMeuPerfilRecrutador(){
        usuarioService.removerMeuPerfilRecrutador();
        return ResponseEntity.noContent().build();
    }

    //Usado por admins/superadmin vou configurar certinho depois
    //@PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    @DeleteMapping("/{id}/perfil_recrutador")
    public ResponseEntity<Void> removerPerfilRecrutadorPorId(@PathVariable Long id) {
        usuarioService.removerPerfilRecrutadorPorId(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("me/perfil-candidato")
    public ResponseEntity<Void> adicionarMeuPerfilCandidato(){
        usuarioService.adicionarMeuPerfilCandidato();
        return ResponseEntity.noContent().build();
    }

    //Usado por admins/superadmin vou configurar certinho depois
    //@PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    @PutMapping("/{id}/perfil_candidato")
    public ResponseEntity<Void> adicionarPerfilCandidato(@PathVariable Long id) {
        usuarioService.adicionarPerfilCandidatoPorId(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("me/perfil-recrutador/{nomeDaEmpresa}")
    public ResponseEntity<Void> adicionarMeuPerfilRecrutador(@PathVariable String nomeDaEmpresa){
        usuarioService.adicioncarMeuPerfilRecrutador(nomeDaEmpresa);
        return ResponseEntity.noContent().build();
    }

    //Usado por admins/superadmin vou configurar certinho depois
    //@PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    @PutMapping("/{id}/perfil_recrutador/{nomeDaEmpresa}")
    public ResponseEntity<Void> adicionarPerfilRecrutador(@PathVariable Long id, @PathVariable String nomeDaEmpresa) {
        usuarioService.adicionarPerfilRecrutadorPorId(id, nomeDaEmpresa);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> meRemover(){
        usuarioService.meRemover();
        return ResponseEntity.noContent().build();
    }

    //Usado por admins/superadmin vou configurar certinho depois
    //@PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerUsuarioPorId(@PathVariable Long id) {
        usuarioService.deletarUsuarioPorId(id);
        return ResponseEntity.noContent().build();
    }
}