package com.QueroTrabalhar.services.localidade;

import com.QueroTrabalhar.domain.entity.localidade.Localidade;
import com.QueroTrabalhar.domain.entity.localidade.LocalidadePendente;
import com.QueroTrabalhar.services.exceptions.BusinessRuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LocalidadeResolucaoService {

    private static final Logger logger = LoggerFactory.getLogger(LocalidadeResolucaoService.class);

    private final ResolvedorLocalidadeBaseInterna resolvedorLocalidadeBaseInterna;
    private final ResolvedorLocalidadeGoogleMaps resolvedorLocalidadeGoogleMaps;
    private final RegistroLocalidadePendenteService registroLocalidadePendenteService;

    public LocalidadeResolucaoService(
            ResolvedorLocalidadeBaseInterna resolvedorLocalidadeBaseInterna,
            ResolvedorLocalidadeGoogleMaps resolvedorLocalidadeGoogleMaps,
            RegistroLocalidadePendenteService registroLocalidadePendenteService
    ) {
        this.resolvedorLocalidadeBaseInterna = resolvedorLocalidadeBaseInterna;
        this.resolvedorLocalidadeGoogleMaps = resolvedorLocalidadeGoogleMaps;
        this.registroLocalidadePendenteService = registroLocalidadePendenteService;
    }

    @Transactional
    public ResultadoResolucaoLocalidade resolver(String textoLivre) {
        long inicioResolucao = System.nanoTime();
        String textoNormalizado = normalizarTextoObrigatorio(textoLivre);
        String textoHash = gerarTextoHash(textoNormalizado);

        ResultadoResolucaoBaseInterna resultadoBaseInterna =
                resolvedorLocalidadeBaseInterna.resolver(textoNormalizado, textoHash);
        if (resultadoBaseInterna.resolvida()) {
            Localidade localidade = resultadoBaseInterna.localidade();
            logger.info(
                    "event=localidade_resolvida origem=BASE_INTERNA textoHash={} nivel={} duracaoMs={}",
                    textoHash,
                    determinarNivel(localidade),
                    calcularDuracaoMs(inicioResolucao)
            );
            return ResultadoResolucaoLocalidade.resolvida(localidade);
        }
        if (resultadoBaseInterna.deveGerarPendencia()) {
            return criarResultadoPendente(
                    textoNormalizado,
                    textoHash,
                    resultadoBaseInterna.motivoPendencia(),
                    0,
                    inicioResolucao
            );
        }

        ResultadoResolucaoGoogleMaps resultadoGoogleMaps =
                resolvedorLocalidadeGoogleMaps.resolver(textoNormalizado, textoHash);
        if (resultadoGoogleMaps.resolvida()) {
            Localidade localidade = resultadoGoogleMaps.localidade();
            logger.info(
                    "event=localidade_resolvida origem=GOOGLE_MAPS textoHash={} nivel={} tentativasExecutadas={} duracaoMs={}",
                    textoHash,
                    determinarNivel(localidade),
                    resultadoGoogleMaps.tentativasExecutadas(),
                    calcularDuracaoMs(inicioResolucao)
            );
            return ResultadoResolucaoLocalidade.resolvida(localidade);
        }

        return criarResultadoPendente(
                textoNormalizado,
                textoHash,
                resultadoGoogleMaps.motivoPendencia(),
                resultadoGoogleMaps.tentativasExecutadas(),
                inicioResolucao
        );
    }

    private ResultadoResolucaoLocalidade criarResultadoPendente(
            String textoNormalizado,
            String textoHash,
            MotivoPendenciaLocalidade motivoPendencia,
            int tentativasExecutadas,
            long inicioResolucao
    ) {
        // A localidade validada continua sendo a fonte oficial.
        // A pendencia apenas preserva o texto original ate a confirmacao futura do usuario.
        LocalidadePendente localidadePendente = registroLocalidadePendenteService.registrar(
                textoNormalizado,
                motivoPendencia,
                textoHash,
                tentativasExecutadas,
                inicioResolucao
        );
        return ResultadoResolucaoLocalidade.pendente(localidadePendente);
    }

    private String normalizarTextoObrigatorio(String textoLivre) {
        String textoNormalizado = normalizarTextoOpcional(textoLivre);
        if (textoNormalizado == null) {
            throw new BusinessRuleException("O texto da localidade \u00E9 obrigat\u00F3rio.");
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

    private String determinarNivel(Localidade localidade) {
        if (localidade.getCidade() != null) {
            return "CIDADE";
        }
        if (localidade.getEstado() != null) {
            return "ESTADO";
        }
        return "PAIS";
    }

    private long calcularDuracaoMs(long inicioNanos) {
        return (System.nanoTime() - inicioNanos) / 1_000_000;
    }

    private String gerarTextoHash(String textoNormalizado) {
        return Long.toHexString(Integer.toUnsignedLong(textoNormalizado.hashCode()));
    }
}
