package com.QueroTrabalhar.domain.dtos.oportunidadeDeEmprego;

import com.QueroTrabalhar.domain.entity.Empresa;
import com.QueroTrabalhar.domain.entity.OportunidadeDeEmprego;
import com.QueroTrabalhar.domain.entity.PerfilRecrutador;
import com.QueroTrabalhar.domain.entity.TipoDeEmprego;
import com.QueroTrabalhar.domain.entity.Usuario;
import com.QueroTrabalhar.domain.entity.localidade.Cidade;
import com.QueroTrabalhar.domain.entity.localidade.Estado;
import com.QueroTrabalhar.domain.entity.localidade.Localidade;
import com.QueroTrabalhar.domain.entity.localidade.LocalidadePendente;
import com.QueroTrabalhar.domain.entity.localidade.Pais;
import com.QueroTrabalhar.domain.enums.Modalidade;
import com.QueroTrabalhar.domain.enums.StatusValidacaoLocalidade;

public record OportunidadeDeEmpregoResponseDTO(
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
        String statusLocalidade,
        String localidadeTextoOriginal,
        StatusValidacaoLocalidade statusValidacaoLocalidade,
        String motivoPendenciaLocalidade,
        Long recrutadorId,
        String recrutadorNome,
        Long empresaId,
        String empresaNome
) {
    private static final String STATUS_LOCALIDADE_VALIDADA = "VALIDADA";
    private static final String STATUS_LOCALIDADE_PENDENTE = "PENDENTE";

    public static OportunidadeDeEmpregoResponseDTO daEntidade(OportunidadeDeEmprego entidade) {
        return daEntidade(entidade, null);
    }

    public static OportunidadeDeEmpregoResponseDTO daEntidade(
            OportunidadeDeEmprego entidade,
            LocalidadePendente localidadePendente
    ) {
        TipoDeEmprego tipoDeEmprego = entidade.getTipoDeEmprego();
        Localidade localidade = entidade.getLocalizacao();
        LocalidadePendente pendenciaEfetiva = localidade != null ? null : localidadePendente;
        Pais pais = localidade != null ? localidade.getPais() : null;
        Estado estado = localidade != null ? localidade.getEstado() : null;
        Cidade cidade = localidade != null ? localidade.getCidade() : null;
        PerfilRecrutador recrutador = entidade.getPerfilRecrutador();
        Usuario usuario = recrutador != null ? recrutador.getUsuario() : null;
        Empresa empresa = entidade.getEmpresa();
        String statusLocalidade = null;

        if (localidade != null) {
            statusLocalidade = STATUS_LOCALIDADE_VALIDADA;
        } else if (pendenciaEfetiva != null) {
            statusLocalidade = STATUS_LOCALIDADE_PENDENTE;
        }

        return new OportunidadeDeEmpregoResponseDTO(
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
                statusLocalidade,
                pendenciaEfetiva != null ? pendenciaEfetiva.getTextoOriginal() : null,
                pendenciaEfetiva != null ? pendenciaEfetiva.getStatusValidacao() : null,
                pendenciaEfetiva != null ? pendenciaEfetiva.getMotivoPendencia() : null,
                recrutador != null ? recrutador.getId() : null,
                usuario != null ? usuario.getNome() : null,
                empresa != null ? empresa.getId() : null,
                empresa != null ? empresa.getNome() : null
        );
    }
}
