package com.QueroTrabalhar.domain.dtos.experienciaProfissional;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record ExperienciaProfissionalFilterDTO(
        @Size(max = 500, message = "O termo de busca deve ter no maximo 500 caracteres.")
        String termo,

        @Positive(message = "O tipo de emprego informado e invalido.")
        Long tipoDeEmpregoId,

        Boolean emAndamento,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate dataInicioDe,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate dataInicioAte,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate dataFimDe,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate dataFimAte
) {
}
