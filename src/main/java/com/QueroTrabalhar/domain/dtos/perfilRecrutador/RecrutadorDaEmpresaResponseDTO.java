package com.QueroTrabalhar.domain.dtos.perfilRecrutador;

import com.QueroTrabalhar.domain.entity.Empresa;
import com.QueroTrabalhar.domain.entity.PerfilRecrutador;
import com.QueroTrabalhar.domain.entity.Usuario;
import com.QueroTrabalhar.domain.enums.StatusVinculoEmpresa;

public record RecrutadorDaEmpresaResponseDTO(
        Long recrutadorId,
        String nome,
        Long empresaId,
        String empresaNome,
        StatusVinculoEmpresa statusVinculoEmpresa
) {
    public static RecrutadorDaEmpresaResponseDTO daEntidade(PerfilRecrutador entidade) {
        Usuario usuario = entidade.getUsuario();
        Empresa empresa = entidade.getEmpresaVinculada();

        return new RecrutadorDaEmpresaResponseDTO(
                entidade.getId(),
                usuario != null ? usuario.getNome() : null,
                empresa != null ? empresa.getId() : null,
                empresa != null ? empresa.getNome() : null,
                entidade.getStatusVinculoEmpresa()
        );
    }
}
