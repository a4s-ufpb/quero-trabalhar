package com.QueroTrabalhar.controllers;

import com.QueroTrabalhar.dtos.user.UserRequestDTO;
import com.QueroTrabalhar.dtos.user.UserResponseDTO;
import com.QueroTrabalhar.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Indica que esta classe é um controlador REST
@RequestMapping ("api/usuario") // Define a URL base para endpoints deste controlador

public class UsuarioController {

    @Autowired  // Injeta as instâncias
    private UsuarioService usuarioService; // Dependencia do Serviço

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> listarTodos() {
        List<UserResponseDTO> usuarios = usuarioService.listarTodosUsuarios();
        return ResponseEntity.ok(usuarios);
    }
    @GetMapping ("/{id}")
    public ResponseEntity<UserResponseDTO> buscarPorId(@PathVariable Long id) {
        UserResponseDTO response = usuarioService.buscarUsuarioPorId(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> adicionarUsuario(@RequestBody UserRequestDTO userDto) {
        UserResponseDTO usuarioSalvo = usuarioService.salvarUsuario(userDto);
        return ResponseEntity.ok(usuarioSalvo);
    }

    @DeleteMapping ("/{id}")
    public ResponseEntity<Void> removerUsuario(@PathVariable Long id) {
        usuarioService.deletarUsuario(id);
        return ResponseEntity.noContent().build();
    }


}
