package com.QueroTrabalhar.domain.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true) // Segurança: ignora o que não mapeamos
public record CidadeIbgeDTO(
        Long id,
        String nome,
        MicrorregiaoDTO microrregiao
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record MicrorregiaoDTO(MesorregiaoDTO mesorregiao) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record MesorregiaoDTO(UfDTO UF) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record UfDTO(String sigla) {}

    // AJUSTADO: Em Records, usamos os métodos de acesso (com parênteses)
    public String getSiglaEstado() {
        return microrregiao().mesorregiao().UF().sigla();
    }
}