package com.QueroTrabalhar.domain.enums;

/**
 * Representa o estado técnico de validação associado a uma localidade pendente.
 *
 * <p>Esses status não modelam um estado público normal do domínio. Eles existem para controlar a fila
 * de resolução e, quando necessário, permitir que o próprio dono do recurso veja a situação em fluxos
 * autenticados, como consultas do tipo {@code /me}.</p>
 */
public enum StatusValidacaoLocalidade {
    /** Pendência aberta aguardando nova tentativa automática ou tratamento futuro. */
    PENDENTE_VALIDACAO,
    /** Reservado para um fluxo pós-MVP em que uma sugestão será apresentada ao usuário. */
    SUGESTAO_ENCONTRADA,
    /** Reservado para confirmação manual pós-MVP da localidade sugerida. */
    CONFIRMADA_PELO_USUARIO,
    /** Reservado para recusa manual pós-MVP da localidade sugerida. */
    RECUSADA_PELO_USUARIO,
    /** Indica que a validação não avançou para uma localidade oficial confirmada. */
    FALHA_VALIDACAO
}
