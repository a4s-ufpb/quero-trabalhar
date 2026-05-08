package com.QueroTrabalhar.domain.dtos.usuario;

import jakarta.validation.constraints.Size;

public record UsuarioFilterDTO(
        @Size(max = 150, message = "O termo de busca deve ter no maximo 150 caracteres.")
        String termo,

        @Size(max = 100, message = "O nome deve ter no maximo 100 caracteres.")
        String nome,

        @Size(max = 255, message = "O email deve ter no maximo 255 caracteres.")
        String email,

        @Size(max = 14, message = "O CPF deve ter no maximo 14 caracteres.")
        String cpf,

        Boolean temPerfilCandidato,

        Boolean temPerfilRecrutador
) {
}
