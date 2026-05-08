package com.QueroTrabalhar.services.localidade;

import com.QueroTrabalhar.domain.entity.localidade.Localidade;
import com.QueroTrabalhar.services.exceptions.BusinessRuleException;
import org.springframework.stereotype.Service;

@Service
class FluxoResolucaoLocalidadeService {

    private final ResolvedorLocalidadeBaseInterna resolvedorLocalidadeBaseInterna;
    private final ResolvedorLocalidadeGoogleMaps resolvedorLocalidadeGoogleMaps;

    FluxoResolucaoLocalidadeService(
            ResolvedorLocalidadeBaseInterna resolvedorLocalidadeBaseInterna,
            ResolvedorLocalidadeGoogleMaps resolvedorLocalidadeGoogleMaps
    ) {
        this.resolvedorLocalidadeBaseInterna = resolvedorLocalidadeBaseInterna;
        this.resolvedorLocalidadeGoogleMaps = resolvedorLocalidadeGoogleMaps;
    }

    ResultadoTentativaResolucaoLocalidade resolver(String textoLivre) {
        String textoNormalizado = normalizarTextoObrigatorio(textoLivre);
        String textoHash = gerarTextoHash(textoNormalizado);

        ResultadoResolucaoBaseInterna resultadoBaseInterna =
                resolvedorLocalidadeBaseInterna.resolver(textoNormalizado, textoHash);
        if (resultadoBaseInterna.resolvida()) {
            return ResultadoTentativaResolucaoLocalidade.resolvida(
                    textoNormalizado,
                    textoHash,
                    resultadoBaseInterna.localidade(),
                    OrigemResolucaoLocalidade.BASE_INTERNA,
                    0
            );
        }
        if (resultadoBaseInterna.deveGerarPendencia()) {
            return ResultadoTentativaResolucaoLocalidade.pendente(
                    textoNormalizado,
                    textoHash,
                    resultadoBaseInterna.motivoPendencia(),
                    0
            );
        }

        ResultadoResolucaoGoogleMaps resultadoGoogleMaps =
                resolvedorLocalidadeGoogleMaps.resolver(textoNormalizado, textoHash);
        if (resultadoGoogleMaps.resolvida()) {
            return ResultadoTentativaResolucaoLocalidade.resolvida(
                    textoNormalizado,
                    textoHash,
                    resultadoGoogleMaps.localidade(),
                    OrigemResolucaoLocalidade.GOOGLE_MAPS,
                    resultadoGoogleMaps.tentativasExecutadas()
            );
        }

        return ResultadoTentativaResolucaoLocalidade.pendente(
                textoNormalizado,
                textoHash,
                resultadoGoogleMaps.motivoPendencia(),
                resultadoGoogleMaps.tentativasExecutadas()
        );
    }

    private String normalizarTextoObrigatorio(String textoLivre) {
        String textoNormalizado = normalizarTextoOpcional(textoLivre);
        if (textoNormalizado == null) {
            throw new BusinessRuleException("O texto da localidade é obrigatório.");
        }
        return textoNormalizado;
    }

    private String normalizarTextoOpcional(String valor) {
        if (valor == null) {
            return null;
        }

        String valorNormalizado = valor.trim();
        return valorNormalizado.isBlank() ? null : valorNormalizado;
    }

    private String gerarTextoHash(String textoNormalizado) {
        return Long.toHexString(Integer.toUnsignedLong(textoNormalizado.hashCode()));
    }

    enum OrigemResolucaoLocalidade {
        BASE_INTERNA,
        GOOGLE_MAPS
    }

    record ResultadoTentativaResolucaoLocalidade(
            String textoNormalizado,
            String textoHash,
            Localidade localidadeValidada,
            MotivoPendenciaLocalidade motivoPendencia,
            OrigemResolucaoLocalidade origemResolucao,
            int tentativasExecutadas
    ) {

        ResultadoTentativaResolucaoLocalidade {
            boolean possuiLocalidadeValidada = localidadeValidada != null;
            boolean possuiMotivoPendencia = motivoPendencia != null;

            if (possuiLocalidadeValidada == possuiMotivoPendencia) {
                throw new IllegalArgumentException(
                        "A tentativa de resolucao deve conter localidade validada ou motivo de pendencia."
                );
            }
        }

        static ResultadoTentativaResolucaoLocalidade resolvida(
                String textoNormalizado,
                String textoHash,
                Localidade localidadeValidada,
                OrigemResolucaoLocalidade origemResolucao,
                int tentativasExecutadas
        ) {
            return new ResultadoTentativaResolucaoLocalidade(
                    textoNormalizado,
                    textoHash,
                    localidadeValidada,
                    null,
                    origemResolucao,
                    tentativasExecutadas
            );
        }

        static ResultadoTentativaResolucaoLocalidade pendente(
                String textoNormalizado,
                String textoHash,
                MotivoPendenciaLocalidade motivoPendencia,
                int tentativasExecutadas
        ) {
            return new ResultadoTentativaResolucaoLocalidade(
                    textoNormalizado,
                    textoHash,
                    null,
                    motivoPendencia,
                    null,
                    tentativasExecutadas
            );
        }

        boolean resolvida() {
            return localidadeValidada != null;
        }

        String nivelLocalidade() {
            if (!resolvida()) {
                return null;
            }
            if (localidadeValidada.getCidade() != null) {
                return "CIDADE";
            }
            if (localidadeValidada.getEstado() != null) {
                return "ESTADO";
            }
            return "PAIS";
        }
    }
}
