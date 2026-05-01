package com.QueroTrabalhar.controllers;

import com.QueroTrabalhar.domain.dtos.perfilRecrutador.PerfilRecrutadorUsuarioRequestDTO;
import com.QueroTrabalhar.domain.dtos.usuario.AlterarSenhaRequestDTO;
import com.QueroTrabalhar.domain.dtos.usuario.UsuarioAtualizacaoRequestDTO;
import com.QueroTrabalhar.domain.dtos.usuario.UsuarioRequestDTO;
import com.QueroTrabalhar.domain.dtos.usuario.UsuarioResponseDTO;
import com.QueroTrabalhar.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<UsuarioResponseDTO> cadastrarUsuario(@RequestBody @Valid UsuarioRequestDTO usuarioRequest) {
        UsuarioResponseDTO usuarioResponse = usuarioService.cadastrarUsuario(usuarioRequest);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(usuarioResponse.id())
                .toUri();

        return ResponseEntity.created(uri).body(usuarioResponse);
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> buscarMeuUsuario() {
        return ResponseEntity.ok(usuarioService.buscarUsuarioAutenticado());
    }

    @PutMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> atualizarMeuUsuario(
            @Valid @RequestBody UsuarioAtualizacaoRequestDTO usuarioAtualizacaoRequest
    ) {
        return ResponseEntity.ok(usuarioService.atualizarMeuUsuario(usuarioAtualizacaoRequest));
    }

    @PutMapping("/me/senha")
    public ResponseEntity<Void> alterarMinhaSenha(@Valid @RequestBody AlterarSenhaRequestDTO alterarSenhaRequest) {
        usuarioService.alterarMinhaSenha(alterarSenhaRequest);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me/perfil-candidato")
    public ResponseEntity<Void> removerMeuPerfilCandidato() {
        usuarioService.removerMeuPerfilCandidato();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me/perfil-recrutador")
    public ResponseEntity<Void> removerMeuPerfilRecrutador() {
        usuarioService.removerMeuPerfilRecrutador();
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/me/perfil-candidato")
    public ResponseEntity<Void> adicionarMeuPerfilCandidato() {
        usuarioService.adicionarMeuPerfilCandidato();
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/me/perfil-recrutador")
    public ResponseEntity<Void> adicionarMeuPerfilRecrutador(
            @Valid @RequestBody PerfilRecrutadorUsuarioRequestDTO perfilRecrutadorRequest
    ) {
        usuarioService.adicionarMeuPerfilRecrutador(perfilRecrutadorRequest);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> meRemover() {
        usuarioService.meRemover();
        return ResponseEntity.noContent().build();
    }
}
