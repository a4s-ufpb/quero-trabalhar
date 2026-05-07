package com.QueroTrabalhar.domain.dtos.empresa;

import com.QueroTrabalhar.domain.entity.Empresa;
import com.QueroTrabalhar.domain.entity.localidade.Cidade;
import com.QueroTrabalhar.domain.entity.localidade.Estado;
import com.QueroTrabalhar.domain.entity.localidade.Localidade;
import com.QueroTrabalhar.domain.entity.localidade.LocalidadePendente;
import com.QueroTrabalhar.domain.entity.localidade.Pais;
import com.QueroTrabalhar.domain.enums.StatusValidacaoLocalidade;

public record EmpresaResponseDTO(
        Long id,
        String nome,
        String descricao,
        String site,
        String emailPublico,
        String telefonePublico,
        boolean ativo,
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
        String motivoPendenciaLocalidade
) {
    private static final String STATUS_LOCALIDADE_VALIDADA = "VALIDADA";
    private static final String STATUS_LOCALIDADE_PENDENTE = "PENDENTE";

    public static EmpresaResponseDTO daEntidade(Empresa entidade) {
        return daEntidade(entidade, null);
    }

    public static EmpresaResponseDTO daEntidade(Empresa entidade, LocalidadePendente localidadePendente) {
        Localidade localidade = entidade.getLocalidade();
        LocalidadePendente pendenciaEfetiva = localidade != null ? null : localidadePendente;
        Pais pais = localidade != null ? localidade.getPais() : null;
        Estado estado = localidade != null ? localidade.getEstado() : null;
        Cidade cidade = localidade != null ? localidade.getCidade() : null;

        String statusLocalidade = null;
        if (localidade != null) {
            statusLocalidade = STATUS_LOCALIDADE_VALIDADA;
        } else if (pendenciaEfetiva != null) {
            statusLocalidade = STATUS_LOCALIDADE_PENDENTE;
        }

        return new EmpresaResponseDTO(
                entidade.getId(),
                entidade.getNome(),
                entidade.getDescricao(),
                entidade.getSite(),
                entidade.getEmailPublico(),
                entidade.getTelefonePublico(),
                entidade.isAtivo(),
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
                pendenciaEfetiva != null ? pendenciaEfetiva.getMotivoPendencia() : null
        );
    }
}
