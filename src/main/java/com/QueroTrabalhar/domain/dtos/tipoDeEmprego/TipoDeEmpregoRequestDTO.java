package com.QueroTrabalhar.domain.dtos.tipoDeEmprego;

import jakarta.validation.constraints.NotBlank;

public record TipoDeEmpregoRequestDTO(
        @NotBlank(message = "O título não pode ser vazio")
        String titulo,

        @NotBlank(message = "O tipo de emprego deve ter uma descrição")
        String descricao
) {}
