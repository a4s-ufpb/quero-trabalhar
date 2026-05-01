package com.QueroTrabalhar.domain.dtos.perfilRecrutador;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PerfilRecrutadorUsuarioRequestDTO(
        @NotBlank(message = "O nome da empresa é obrigatório.")
        @Size(max = 100, message = "O nome da empresa deve ter no máximo 100 caracteres.")
        String nomeDaEmpresa
) {
}
