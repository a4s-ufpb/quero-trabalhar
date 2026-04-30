package com.QueroTrabalhar.domain.dtos.empresa;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record EmpresaRequestDTO(
        @NotBlank(message = "O nome da empresa e obrigatorio.")
        @Size(max = 150, message = "O nome da empresa deve ter no maximo 150 caracteres.")
        String nome,

        @Size(max = 1000, message = "A descricao da empresa deve ter no maximo 1000 caracteres.")
        String descricao,

        @Size(max = 255, message = "O site da empresa deve ter no maximo 255 caracteres.")
        String site,

        @Email(message = "O email publico deve ser valido.")
        @Size(max = 150, message = "O email publico deve ter no maximo 150 caracteres.")
        String emailPublico,

        @Size(max = 20, message = "O telefone publico deve ter no maximo 20 caracteres.")
        String telefonePublico,

        @NotNull(message = "O pais e obrigatorio.")
        @Positive(message = "O pais informado e invalido.")
        Long paisId,

        @Positive(message = "O estado informado e invalido.")
        Long estadoId,

        @Positive(message = "A cidade informada e invalida.")
        Long cidadeId
) {
}
