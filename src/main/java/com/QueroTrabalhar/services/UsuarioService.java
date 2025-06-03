package com.QueroTrabalhar.services;

import com.QueroTrabalhar.dtos.UserRequestDTO;
import com.QueroTrabalhar.dtos.UserResponseDTO;
import com.QueroTrabalhar.entity.ExperienciaProfissional;
import com.QueroTrabalhar.entity.InteresseEmEmprego;
import com.QueroTrabalhar.entity.Usuario;
import com.QueroTrabalhar.repository.ExperienciaProfissionalRepository;
import com.QueroTrabalhar.repository.InteresseEmEmpregoRepository;
import com.QueroTrabalhar.repository.UsuarioRepository;
import com.QueroTrabalhar.util.UserConverter;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UserConverter userConverter;



    public List<UserResponseDTO> listarTodosUsuarios() {
        List<Usuario> users = usuarioRepository.findAll();
        return users.stream()
                .map(userConverter :: userEntityToDto)
                .collect(Collectors.toList());
    }

    public UserResponseDTO buscarUsuarioPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        return userConverter.userEntityToDto(usuario);
    }

    public UserResponseDTO salvarUsuario(UserRequestDTO userDto) {

        Usuario user = userConverter.userDtoToEntity(userDto);

        Usuario userSaved = usuarioRepository.save(user);

        return userConverter.userEntityToDto(userSaved);
    }

    public void deletarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado");
        }
        usuarioRepository.deleteById(id);
    }
}
