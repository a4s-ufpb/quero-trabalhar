package com.QueroTrabalhar.domain.dtos.empresa;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record EmpresaFilterDTO(
        @Size(max = 150, message = "O termo de busca deve ter no máximo 150 caracteres.")
        String termo,

        @Positive(message = "O país informado é inválido.")
        Long paisId,

        @Positive(message = "O estado informado é inválido.")
        Long estadoId,

        @Positive(message = "A cidade informada é inválida.")
        Long cidadeId
) {
}
