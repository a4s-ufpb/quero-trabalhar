package com.QueroTrabalhar.domain.dtos.oportunidadeDeEmprego;

import com.QueroTrabalhar.domain.entity.Empresa;
import com.QueroTrabalhar.domain.entity.OportunidadeDeEmprego;
import com.QueroTrabalhar.domain.entity.PerfilRecrutador;
import com.QueroTrabalhar.domain.entity.TipoDeEmprego;
import com.QueroTrabalhar.domain.entity.Usuario;
import com.QueroTrabalhar.domain.entity.localidade.Cidade;
import com.QueroTrabalhar.domain.entity.localidade.Estado;
import com.QueroTrabalhar.domain.entity.localidade.Localidade;
import com.QueroTrabalhar.domain.entity.localidade.Pais;
import com.QueroTrabalhar.domain.enums.Modalidade;

public record OportunidadeDeEmpregoPublicaResponseDTO(
        Long id,
        String descricao,
        Modalidade modalidade,
        Long tipoDeEmpregoId,
        String tipoDeEmprego,
        Long paisId,
        String pais,
        String paisSigla,
        Long estadoId,
        String estado,
        String estadoSigla,
        Long cidadeId,
        String cidade,
        Long recrutadorId,
        String recrutadorNome,
        Long empresaId,
        String empresaNome
) {
    public static OportunidadeDeEmpregoPublicaResponseDTO daEntidade(OportunidadeDeEmprego entidade) {
        TipoDeEmprego tipoDeEmprego = entidade.getTipoDeEmprego();
        Localidade localidade = entidade.getLocalizacao();
        Pais pais = localidade != null ? localidade.getPais() : null;
        Estado estado = localidade != null ? localidade.getEstado() : null;
        Cidade cidade = localidade != null ? localidade.getCidade() : null;
        PerfilRecrutador recrutador = entidade.getPerfilRecrutador();
        Usuario usuario = recrutador != null ? recrutador.getUsuario() : null;
        Empresa empresa = entidade.getEmpresa();

        return new OportunidadeDeEmpregoPublicaResponseDTO(
                entidade.getId(),
                entidade.getDescricao(),
                entidade.getModalidade(),
                tipoDeEmprego != null ? tipoDeEmprego.getId() : null,
                tipoDeEmprego != null ? tipoDeEmprego.getTitulo() : null,
                pais != null ? pais.getId() : null,
                pais != null ? pais.getNome() : null,
                pais != null ? pais.getSigla() : null,
                estado != null ? estado.getId() : null,
                estado != null ? estado.getNome() : null,
                estado != null ? estado.getSigla() : null,
                cidade != null ? cidade.getId() : null,
                cidade != null ? cidade.getNome() : null,
                recrutador != null ? recrutador.getId() : null,
                usuario != null ? usuario.getNome() : null,
                empresa != null ? empresa.getId() : null,
                empresa != null ? empresa.getNome() : null
        );
    }
}
