package com.QueroTrabalhar.controllers;

import com.QueroTrabalhar.domain.dtos.UserDTORequest;
import com.QueroTrabalhar.domain.dtos.UserDTOResponse;
import com.QueroTrabalhar.repository.UsuarioRepository;
import com.QueroTrabalhar.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios") // Adicionada a barra inicial e o plural (padrão REST)
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public ResponseEntity<List<UserDTOResponse>> listarTodos() {
        List<UserDTOResponse> usuarios = usuarioService.listarTodosUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTOResponse> buscarPorId(@PathVariable Long id) {
        UserDTOResponse response = usuarioService.buscarUsuarioPorId(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTOResponse> adicionarUsuario(@RequestBody @Valid UserDTORequest userDTORequest) {
        // CORREÇÃO: Utilizando o .isPresent() do Optional para verificar se o email já existe
        if (usuarioRepository.findByEmail(userDTORequest.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(userDTORequest.getSenha());
        UserDTOResponse usuarioSalvo = usuarioService.salvarUsuario(userDTORequest, encryptedPassword);

        // Alterado para 201 Created (Melhor prática para recursos recém-criados)
        return ResponseEntity.status(201).body(usuarioSalvo);
    }

    // AVISO: Os métodos de registrar/remover interesse foram apagados daqui!
    // Se o Front-end quiser adicionar um interesse, ele deve chamar:
    // POST /api/candidatos/{id}/interesses/vagas/{vagaId} (O controlador que criamos antes)

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerUsuario(@PathVariable Long id) {
        usuarioService.deletarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}