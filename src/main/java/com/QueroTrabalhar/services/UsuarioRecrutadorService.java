package com.QueroTrabalhar.services;

import com.QueroTrabalhar.entity.UsuarioRecrutador;
import com.QueroTrabalhar.repository.UsuarioRecrutadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioRecrutadorService {

    @Autowired
    private UsuarioRecrutadorRepository usuarioRecrutadorRepository;

    public List<UsuarioRecrutador> listarUsuarioRecrutadores(){
        return usuarioRecrutadorRepository.findAll();
    }

    public Optional<UsuarioRecrutador> listarUsuarioRecrutadorPorId(Long id){
        return usuarioRecrutadorRepository.findById(id);
    }

    public UsuarioRecrutador salvarUsuarioRecrutador(UsuarioRecrutador usuarioRecrutador){
        return usuarioRecrutadorRepository.save(usuarioRecrutador);
    }

    public void deletarUsuarioRecrutadorPorId(Long id){
        usuarioRecrutadorRepository.deleteById(id);
    }
}
