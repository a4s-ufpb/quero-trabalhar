package com.QueroTrabalhar.domain.dtos.empresa;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record EmpresaRequestDTO(
        @NotBlank(message = "O nome da empresa é obrigatório.")
        @Size(max = 150, message = "O nome da empresa deve ter no máximo 150 caracteres.")
        String nome,

        @Size(max = 1000, message = "A descrição da empresa deve ter no máximo 1000 caracteres.")
        String descricao,

        @Size(max = 255, message = "O site da empresa deve ter no máximo 255 caracteres.")
        String site,

        @Email(message = "O e-mail público deve ser válido.")
        @Size(max = 150, message = "O e-mail público deve ter no máximo 150 caracteres.")
        String emailPublico,

        @Size(max = 20, message = "O telefone público deve ter no máximo 20 caracteres.")
        String telefonePublico,

        @Positive(message = "O país informado é inválido.")
        Long paisId,

        @Positive(message = "O estado informado é inválido.")
        Long estadoId,

        @Positive(message = "A cidade informada é inválida.")
        Long cidadeId,

        @Size(max = 255, message = "O texto da localidade deve ter no máximo 255 caracteres.")
        String localidadeTexto
) {
}
