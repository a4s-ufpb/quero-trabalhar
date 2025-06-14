package com.QueroTrabalhar.services;

import com.QueroTrabalhar.dtos.user.UserDTO;
import com.QueroTrabalhar.entity.Usuario;
import com.QueroTrabalhar.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;


    public List<UserDTO> listarTodosUsuarios() {
        List<Usuario> users = usuarioRepository.findAll();
        return users.stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }

    public UserDTO buscarUsuarioPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        return new UserDTO(usuario);
    }

    public UserDTO salvarUsuario(UserDTO userDTO) {

        Usuario user = userDTO.userDtoToUser();

        Usuario userSaved = usuarioRepository.save(user);

        return new UserDTO(userSaved);
    }

    public void deletarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado");
        }
        usuarioRepository.deleteById(id);
    }
}
