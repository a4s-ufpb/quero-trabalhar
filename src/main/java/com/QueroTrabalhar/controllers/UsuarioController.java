package com.QueroTrabalhar.controllers;

import com.QueroTrabalhar.dtos.user.UserDTORequest;
import com.QueroTrabalhar.dtos.user.UserDTOResponse;
import com.QueroTrabalhar.repository.UsuarioRepository;
import com.QueroTrabalhar.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Indica que esta classe é um controlador REST
@RequestMapping ("api/usuario") // Define a URL base para endpoints deste controlador

public class UsuarioController {

    @Autowired  // Injeta as instâncias
    private UsuarioService usuarioService; // Dependencia do Serviço

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public ResponseEntity<List<UserDTOResponse>> listarTodos() {
        List<UserDTOResponse> usuarios = usuarioService.listarTodosUsuarios();
        return ResponseEntity.ok(usuarios);
    }
    @GetMapping ("/{id}")
    public ResponseEntity<UserDTOResponse> buscarPorId(@PathVariable Long id) {
        UserDTOResponse response = usuarioService.buscarUsuarioPorId(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping ("/register")
    public ResponseEntity<UserDTOResponse> adicionarUsuario(@RequestBody @Valid UserDTORequest userDTORequest) {
        if (usuarioRepository.findByEmail(userDTORequest.getEmail()) != null) return ResponseEntity.badRequest().build();

        String encryptedPassword = new BCryptPasswordEncoder().encode(userDTORequest.getSenha());
        UserDTOResponse usuarioSalvo = usuarioService.salvarUsuario(userDTORequest, encryptedPassword);
        return ResponseEntity.ok(usuarioSalvo);
    }

    @PostMapping("/{usuarioID}/interesse-em-oportunidade/{oportunidadeID}")
    public ResponseEntity<UserDTOResponse> registrarInteresseEmOportunidadeDeEmprego(@PathVariable Long usuarioID, @PathVariable Long oportunidadeID) {
        UserDTOResponse userUpdated = usuarioService.registrarInteresseEmOportunidadeDeEmprego(usuarioID,  oportunidadeID);
        return ResponseEntity.ok(userUpdated);
    }

    @DeleteMapping("/{userId}/interesse-em-oportunidade/{oportunidadeID}")
    public ResponseEntity<Void> removerInteresseEmOportunidadeDeEmprego(@PathVariable Long usuarioID, @PathVariable Long oportunidadeID) {
        usuarioService.removerInteresseEmOportunidadeDeEmprego(usuarioID, oportunidadeID);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping ("/{id}")
    public ResponseEntity<Void> removerUsuario(@PathVariable Long id) {
        usuarioService.deletarUsuario(id);
        return ResponseEntity.noContent().build();
    }


}
