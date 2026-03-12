package com.QueroTrabalhar.controllers.exceptions;

public record FieldMessage (
        String fieldName,
        String message
) {}
