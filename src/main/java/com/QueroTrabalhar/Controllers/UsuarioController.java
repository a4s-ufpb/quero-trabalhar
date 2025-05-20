package com.QueroTrabalhar.Controllers;

import com.QueroTrabalhar.DTOs.UsuarioDTO;
import com.QueroTrabalhar.Entity.Usuario;
import com.QueroTrabalhar.Services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController // Indica que esta classe é um controlador REST
@RequestMapping ("api/usuario") // Define a URL base para endpoints deste controlador

public class UsuarioController {

    @Autowired  // Injeta as instâncias
    private UsuarioService usuarioService; // Dependencia do Serviço

    @GetMapping
    public ResponseEntity<List<UsuarioDTO.Response>> listarTodos() {
        List<UsuarioDTO.Response> usuarios = usuarioService.listarTodosUsuarios();
        return ResponseEntity.ok(usuarios);
    }
    @GetMapping ("/{id}")
    public ResponseEntity<UsuarioDTO.Response> buscarPorId(@PathVariable Long id) {
        UsuarioDTO.Response response = usuarioService.buscarUsuarioPorId(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<UsuarioDTO.Response> adicionarUsuario(@RequestBody UsuarioDTO.Request dto) {
        UsuarioDTO.Response response = usuarioService.salvarUsuario(dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping ("/{id}")
    public ResponseEntity<Void> removerUsuario(@PathVariable Long id) {
        usuarioService.deletarUsuario(id);
        return ResponseEntity.noContent().build();
    }


}
