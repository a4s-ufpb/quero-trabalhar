package com.QueroTrabalhar.domain.dtos.experienciaProfissional;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ExperienciaProfissionalRequestDTO(
        @NotNull
        Long tipoDeEmpregoId,
        @NotBlank
        String descricao,
        @NotNull
        LocalDate dataInicio,
        LocalDate dataFim
) {}
