package com.QueroTrabalhar.util;

import com.QueroTrabalhar.dtos.UserRequestDTO;
import com.QueroTrabalhar.dtos.UserResponseDTO;
import com.QueroTrabalhar.entity.ExperienciaProfissional;
import com.QueroTrabalhar.entity.InteresseEmEmprego;
import com.QueroTrabalhar.entity.Usuario;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class UserConverter {

    public Usuario userDtoToEntity(UserRequestDTO usuarioDto) {
        Usuario usuario = new Usuario();
        usuario.setCpf(usuarioDto.getCpf());
        usuario.setNome(usuarioDto.getNome());
        usuario.setTelefone(usuarioDto.getTelefone());
        usuario.setEmail(usuarioDto.getEmail());
        return usuario;
    }

    public UserResponseDTO userEntityToDto(Usuario usuario) {
        UserResponseDTO userDto = new UserResponseDTO();
        userDto.setId(usuario.getId());
        userDto.setCpf(usuario.getCpf());
        userDto.setNome(usuario.getNome());
        userDto.setTelefone(usuario.getTelefone());
        userDto.setEmail(usuario.getEmail());

        if (usuario.getExperienciaProfissionais() != null && !usuario.getExperienciaProfissionais().isEmpty()) {
            userDto.setExperienciaProfissionalIds(
                    usuario.getExperienciaProfissionais().stream()
                            .map(ExperienciaProfissional::getId)
                            .collect(Collectors.toList())
            );
        } else {
            userDto.setExperienciaProfissionalIds(Collections.emptyList());
        }

        if (usuario.getInteresseEmEmpregos() != null && !usuario.getInteresseEmEmpregos().isEmpty()) {
            userDto.setInteresseEmEmpregoIds(
                    usuario.getInteresseEmEmpregos().stream()
                            .map(InteresseEmEmprego::getId)
                            .collect(Collectors.toList())
            );
        } else {
            userDto.setInteresseEmEmpregoIds(Collections.emptyList());
        }

        return userDto;
    }

}
