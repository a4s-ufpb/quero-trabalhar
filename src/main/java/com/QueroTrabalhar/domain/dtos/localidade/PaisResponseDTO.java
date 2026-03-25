package com.QueroTrabalhar.domain.dtos.localidade;

import com.QueroTrabalhar.domain.entity.localidade.Pais;

public record PaisResponseDTO(
        Long id,
        String nome,
        String sigla
) {
    public static PaisResponseDTO daEntidade(Pais entidade) {
        return new PaisResponseDTO(
                entidade.getId(),
                entidade.getNome(),
                entidade.getSigla()
        );
    }
}
