package com.QueroTrabalhar.domain.dtos.perfilRecrutador;

import com.QueroTrabalhar.domain.dtos.empresa.EmpresaResponseDTO;
import com.QueroTrabalhar.domain.entity.PerfilRecrutador;
import com.QueroTrabalhar.domain.enums.StatusVinculoEmpresa;

public record PerfilRecrutadorEmpresaResponseDTO(
        EmpresaResponseDTO empresa,
        StatusVinculoEmpresa statusVinculoEmpresa
) {
    public static PerfilRecrutadorEmpresaResponseDTO daEntidade(PerfilRecrutador entidade) {
        return new PerfilRecrutadorEmpresaResponseDTO(
                EmpresaResponseDTO.daEntidade(entidade.getEmpresaVinculada()),
                entidade.getStatusVinculoEmpresa()
        );
    }
}
