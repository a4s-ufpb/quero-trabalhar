package com.QueroTrabalhar.domain.dtos.localidade;

import com.QueroTrabalhar.domain.entity.localidade.Estado;

public record EstadoResponseDTO (
      Long id,
      String nome,
      String sigla,
      Long pais_id
){
    public static EstadoResponseDTO daEntidade(Estado entidade){
        return new EstadoResponseDTO(
                entidade.getId(),
                entidade.getNome(),
                entidade.getSigla(),
                entidade.getPais().getId()
        );
    }
}
