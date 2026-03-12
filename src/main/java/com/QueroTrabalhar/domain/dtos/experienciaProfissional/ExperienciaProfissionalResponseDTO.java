package com.QueroTrabalhar.domain.dtos.experienciaProfissional;

import com.QueroTrabalhar.domain.entity.ExperienciaProfissional;
import com.QueroTrabalhar.domain.entity.TipoDeEmprego;

import java.time.LocalDate;

public record ExperienciaProfissionalResponseDTO(
        Long id,
        TipoDeEmprego tipoDeEmprego,
        String descricao,
        LocalDate dataInicio,
        LocalDate dataFim
) {
    public static ExperienciaProfissionalResponseDTO daEntidade(ExperienciaProfissional entidade) {
        return new ExperienciaProfissionalResponseDTO(
                entidade.getId(),
                entidade.getTipoDeEmprego(),
                entidade.getDescricao(),
                entidade.getDataInicio(),
                entidade.getDataFim()
        );
    }
}
