package com.QueroTrabalhar.domain.dtos.empresa;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record EmpresaFilterDTO(
        @Size(max = 150, message = "O termo de busca deve ter no maximo 150 caracteres.")
        String termo,

        @Positive(message = "O pais informado e invalido.")
        Long paisId,

        @Positive(message = "O estado informado e invalido.")
        Long estadoId,

        @Positive(message = "A cidade informada e invalida.")
        Long cidadeId
) {
}
