package com.QueroTrabalhar.controllers.exceptions;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "FieldMessage", description = "Detalhe de validação para um campo específico do payload.")
public record FieldMessage (
        @Schema(example = "email")
        String fieldName,

        @Schema(example = "Formato de email inválido")
        String message
) {}