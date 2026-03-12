package com.QueroTrabalhar.domain.dtos.tipoDeEmprego;

import com.QueroTrabalhar.domain.entity.TipoDeEmprego;

public record TipoDeEmpregoResponseDTO(
        Long id,
        String titulo,
        String descricao,
        boolean aprovado
) {
    public static TipoDeEmpregoResponseDTO daEntidade(TipoDeEmprego entidade) {
        return new TipoDeEmpregoResponseDTO(
                entidade.getId(),
                entidade.getTitulo(),
                entidade.getDescricao(),
                entidade.isAprovado()
        );
    }
}
