package com.QueroTrabalhar.domain.dtos.indicacao;

import com.QueroTrabalhar.domain.entity.Indicacao;
import com.QueroTrabalhar.domain.entity.Usuario;

public record IndicacaoResponseDTO(
        Long id,
        Long autorId,
        String autorNome,
        Long usuarioIndicadoId,
        String usuarioIndicadoNome,
        String mensagem
) {
    public static IndicacaoResponseDTO daEntidade(Indicacao entidade) {
        Usuario autor = entidade.getAutor();
        Usuario usuarioIndicado = entidade.getUsuarioIndicado();

        return new IndicacaoResponseDTO(
                entidade.getId(),
                autor != null ? autor.getId() : null,
                autor != null ? autor.getNome() : null,
                usuarioIndicado != null ? usuarioIndicado.getId() : null,
                usuarioIndicado != null ? usuarioIndicado.getNome() : null,
                entidade.getMensagem()
        );
    }
}
