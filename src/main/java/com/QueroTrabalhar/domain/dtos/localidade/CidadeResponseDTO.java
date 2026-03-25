package com.QueroTrabalhar.domain.dtos.localidade;

import com.QueroTrabalhar.domain.entity.localidade.Cidade;

public record CidadeResponseDTO(
        Long id,
        String nome,
        Long estado
) {
    public static CidadeResponseDTO daEntidade(Cidade entidade){
        return new CidadeResponseDTO(
                entidade.getId(),
                entidade.getNome(),
                entidade.getEstado().getId()
        );
    }
}
