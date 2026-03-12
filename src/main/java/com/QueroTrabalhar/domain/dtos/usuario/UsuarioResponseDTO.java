package com.QueroTrabalhar.domain.dtos.usuario;

import com.QueroTrabalhar.domain.entity.Usuario;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String telefone,
        String email
) {
    public static UsuarioResponseDTO daEntidade(Usuario entidade) {
        return new UsuarioResponseDTO(
                entidade.getId(),
                entidade.getNome(),
                entidade.getTelefone(),
                entidade.getEmail()
        );
    }
}
