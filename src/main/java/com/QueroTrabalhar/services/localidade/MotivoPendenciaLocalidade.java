package com.QueroTrabalhar.services.localidade;

enum MotivoPendenciaLocalidade {
    LOCALIDADE_NAO_ENCONTRADA(
            "Localidade n\u00E3o encontrada na base interna ou na integra\u00E7\u00E3o externa.",
            "LOCALIDADE_NAO_ENCONTRADA"
    ),
    FALHA_INTEGRACAO_EXTERNA(
            "N\u00E3o foi poss\u00EDvel validar a localidade na integra\u00E7\u00E3o externa.",
            "FALHA_INTEGRACAO_EXTERNA"
    ),
    LOCALIDADE_AMBIGUA_BASE_INTERNA(
            "A localidade informada corresponde a m\u00FAltiplas op\u00E7\u00F5es na base interna e precisa de confirma\u00E7\u00E3o do usu\u00E1rio.",
            "AMBIGUIDADE_BASE_INTERNA"
    ),
    LOCALIDADE_AMBIGUA_GOOGLE(
            "A localidade informada retornou m\u00FAltiplas op\u00E7\u00F5es no Google Maps e precisa de confirma\u00E7\u00E3o do usu\u00E1rio.",
            "AMBIGUIDADE_GOOGLE_MAPS"
    );

    private final String descricao;
    private final String categoriaLog;

    MotivoPendenciaLocalidade(String descricao, String categoriaLog) {
        this.descricao = descricao;
        this.categoriaLog = categoriaLog;
    }

    String descricao() {
        return descricao;
    }

    String categoriaLog() {
        return categoriaLog;
    }
}
