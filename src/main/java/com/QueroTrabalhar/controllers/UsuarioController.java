package com.QueroTrabalhar.controllers;

import com.QueroTrabalhar.dtos.user.UserDTO;
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
    public ResponseEntity<List<UserDTO>> listarTodos() {
        List<UserDTO> usuarios = usuarioService.listarTodosUsuarios();
        return ResponseEntity.ok(usuarios);
    }
    @GetMapping ("/{id}")
    public ResponseEntity<UserDTO> buscarPorId(@PathVariable Long id) {
        UserDTO response = usuarioService.buscarUsuarioPorId(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<UserDTO> adicionarUsuario(@RequestBody UserDTO userDto) {
        UserDTO usuarioSalvo = usuarioService.salvarUsuario(userDto);
        return ResponseEntity.ok(usuarioSalvo);
    }

    @PostMapping("/{usuarioID}/interesse-em-emprego/oportunidadeDeEmprego/{oportunidadeID}")
    public ResponseEntity<UserDTO> registrarInteresseEmOportunidadeDeEmprego(@PathVariable Long usuarioID, @PathVariable Long oportunidadeID) {
        UserDTO userUpdated = usuarioService.registrarInteresseEmOportunidadeDeEmprego(usuarioID,  oportunidadeID);
        return ResponseEntity.ok(userUpdated);
    }

    @DeleteMapping ("/{id}")
    public ResponseEntity<Void> removerUsuario(@PathVariable Long id) {
        usuarioService.deletarUsuario(id);
        return ResponseEntity.noContent().build();
    }


}
