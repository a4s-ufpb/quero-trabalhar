package com.QueroTrabalhar.domain.dtos.indicacao;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record IndicacaoRequestDTO(
        @NotNull(message = "O usuário indicado é obrigatório.")
        @Positive(message = "O usuário indicado informado é inválido.")
        Long usuarioIndicadoId,

        @NotBlank(message = "A mensagem é obrigatória.")
        @Size(max = 500, message = "A mensagem deve ter no máximo 500 caracteres.")
        String mensagem
) {
}
