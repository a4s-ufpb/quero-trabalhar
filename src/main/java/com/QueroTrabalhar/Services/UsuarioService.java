package com.QueroTrabalhar.Services;

import com.QueroTrabalhar.DTOs.UsuarioDTO;
import com.QueroTrabalhar.Entity.ExperienciaProfissional;
import com.QueroTrabalhar.Entity.InteresseEmEmprego;
import com.QueroTrabalhar.Entity.Usuario;
import com.QueroTrabalhar.Repository.UsuarioRepository;
import com.QueroTrabalhar.Util.EntityDtoConverter;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.parser.Entity;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;


    public List<UsuarioDTO.Response> listarTodosUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();

        return usuarios.stream()
                .map(usuario -> EntityDtoConverter.convertToDTO(usuario, UsuarioDTO.Response.class))
                .collect(Collectors.toList());
    }

    public UsuarioDTO.Response buscarUsuarioPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        return EntityDtoConverter.convertToDTO(usuario, UsuarioDTO.Response.class);
    }

    public UsuarioDTO.Response salvarUsuario(UsuarioDTO.Request dto) {

        // Converte o DTO para entidade
        Usuario usuario = EntityDtoConverter.convertToEntity(dto, Usuario.class);
        usuarioRepository.save(usuario);

        // Converte a entidade para DTO antes de retornar
        return EntityDtoConverter.convertToDTO(usuario, UsuarioDTO.Response.class);
    }

    public void deletarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }
}
