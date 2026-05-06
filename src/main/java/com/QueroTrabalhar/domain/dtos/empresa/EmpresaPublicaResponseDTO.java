package com.QueroTrabalhar.domain.dtos.empresa;

import com.QueroTrabalhar.domain.entity.Empresa;
import com.QueroTrabalhar.domain.entity.localidade.Cidade;
import com.QueroTrabalhar.domain.entity.localidade.Estado;
import com.QueroTrabalhar.domain.entity.localidade.Localidade;
import com.QueroTrabalhar.domain.entity.localidade.Pais;

public record EmpresaPublicaResponseDTO(
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
        String cidade
) {
    public static EmpresaPublicaResponseDTO daEntidade(Empresa entidade) {
        Localidade localidade = entidade.getLocalidade();
        Pais pais = localidade != null ? localidade.getPais() : null;
        Estado estado = localidade != null ? localidade.getEstado() : null;
        Cidade cidade = localidade != null ? localidade.getCidade() : null;

        return new EmpresaPublicaResponseDTO(
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
                cidade != null ? cidade.getNome() : null
        );
    }
}
