package com.QueroTrabalhar.domain.dtos.perfilRecrutador;

import com.QueroTrabalhar.domain.entity.Empresa;
import com.QueroTrabalhar.domain.entity.PerfilRecrutador;
import com.QueroTrabalhar.domain.entity.Usuario;
import com.QueroTrabalhar.domain.enums.StatusVinculoEmpresa;

public record PerfilRecrutadorResponseDTO(
        Long id,
        String nome,
        String empresaLegada,
        Long empresaVinculadaId,
        String empresaVinculadaNome,
        StatusVinculoEmpresa statusVinculoEmpresa
) {
    public static PerfilRecrutadorResponseDTO daEntidade(PerfilRecrutador entidade) {
        Usuario usuario = entidade.getUsuario();
        Empresa empresaVinculada = entidade.getEmpresaVinculada();

        return new PerfilRecrutadorResponseDTO(
                entidade.getId(),
                usuario != null ? usuario.getNome() : null,
                entidade.getEmpresa(),
                empresaVinculada != null ? empresaVinculada.getId() : null,
                empresaVinculada != null ? empresaVinculada.getNome() : null,
                entidade.getStatusVinculoEmpresa()
        );
    }
}
