package com.QueroTrabalhar.domain.dtos.tipoDeEmprego;

import jakarta.validation.constraints.Size;

public record TipoDeEmpregoFilterDTO(
        @Size(max = 150, message = "O termo de busca deve ter no maximo 150 caracteres.")
        String termo
) {
}
