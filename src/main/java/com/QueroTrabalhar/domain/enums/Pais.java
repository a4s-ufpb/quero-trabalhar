package com.QueroTrabalhar.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Pais {
    BR("Brasil"),
    US("Estados Unidos"),
    PT("Portugal"),
    ES("Espanha"),
    AR("Argentina"),
    DE("Alemanha");

    private final String nomeExibicao;

    Pais(String nomeExibicao) {
        this.nomeExibicao = nomeExibicao;
    }

    public String getNomeExibicao() {
        return nomeExibicao;
    }

    @JsonValue
    public String getCodigo() {
        return this.name();
    }
}
