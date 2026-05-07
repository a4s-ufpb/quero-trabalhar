package com.QueroTrabalhar.domain.dtos.perfilRecrutador;

import com.QueroTrabalhar.domain.dtos.empresa.EmpresaResponseDTO;
import com.QueroTrabalhar.domain.entity.PerfilRecrutador;
import com.QueroTrabalhar.domain.entity.localidade.LocalidadePendente;
import com.QueroTrabalhar.domain.enums.StatusVinculoEmpresa;

public record PerfilRecrutadorEmpresaResponseDTO(
        EmpresaResponseDTO empresa,
        StatusVinculoEmpresa statusVinculoEmpresa
) {
    public static PerfilRecrutadorEmpresaResponseDTO daEntidade(PerfilRecrutador entidade) {
        return daEntidade(entidade, null);
    }

    public static PerfilRecrutadorEmpresaResponseDTO daEntidade(
            PerfilRecrutador entidade,
            LocalidadePendente localidadePendente
    ) {
        return new PerfilRecrutadorEmpresaResponseDTO(
                entidade.getEmpresaVinculada() != null
                        ? EmpresaResponseDTO.daEntidade(entidade.getEmpresaVinculada(), localidadePendente)
                        : null,
                entidade.getStatusVinculoEmpresa()
        );
    }
}
