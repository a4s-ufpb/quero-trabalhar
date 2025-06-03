package com.QueroTrabalhar.controllers;

import com.QueroTrabalhar.entity.UsuarioRecrutador;
import com.QueroTrabalhar.services.UsuarioRecrutadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/usuario-recrutador")
public class UsuarioRecrutadorController {

    @Autowired
    private UsuarioRecrutadorService usuarioRecrutadorService;

    @GetMapping
    public List<UsuarioRecrutador> listarUsuariosRecrutadores() {
        return usuarioRecrutadorService.listarUsuarioRecrutadores();
    }

    @GetMapping ("/{id}")
    public ResponseEntity<UsuarioRecrutador> buscarUsuarioRecrutadorPorId(@PathVariable Long id) {
        Optional<UsuarioRecrutador> usuarioRecrutador = usuarioRecrutadorService.listarUsuarioRecrutadorPorId(id);
        return usuarioRecrutador.map(ResponseEntity :: ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public UsuarioRecrutador salvarUsuarioRecrutador(@RequestBody UsuarioRecrutador usuarioRecrutador) {
        return usuarioRecrutadorService.salvarUsuarioRecrutador(usuarioRecrutador);
    }

    @DeleteMapping
    public ResponseEntity<Void> deletarUsuarioRecrutador(@RequestParam Long id) {
        usuarioRecrutadorService.deletarUsuarioRecrutadorPorId(id);
        return ResponseEntity.noContent().build();
    }
}
