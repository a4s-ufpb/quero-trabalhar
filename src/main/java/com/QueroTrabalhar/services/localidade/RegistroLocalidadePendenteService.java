package com.QueroTrabalhar.services.localidade;

import com.QueroTrabalhar.domain.entity.localidade.LocalidadePendente;
import com.QueroTrabalhar.repository.LocalidadePendenteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RegistroLocalidadePendenteService {

    private static final Logger logger = LoggerFactory.getLogger(RegistroLocalidadePendenteService.class);

    private final LocalidadePendenteRepository localidadePendenteRepository;

    public RegistroLocalidadePendenteService(LocalidadePendenteRepository localidadePendenteRepository) {
        this.localidadePendenteRepository = localidadePendenteRepository;
    }

    public LocalidadePendente registrar(
            String textoNormalizado,
            MotivoPendenciaLocalidade motivoPendencia,
            String textoHash,
            int tentativasExecutadas,
            long inicioResolucao
    ) {
        // A pendencia e fallback tecnico temporario. Ela nao substitui a localidade validada que aparece nos GETs publicos.
        LocalidadePendente localidadePendente = LocalidadePendente.criarPendenteInformadaPeloUsuario(
                textoNormalizado,
                motivoPendencia.descricao()
        );
        LocalidadePendente localidadePendenteSalva = localidadePendenteRepository.save(localidadePendente);

        logger.warn(
                "event=localidade_pendente_criada textoHash={} motivoCategoria={} tentativasExecutadas={} statusValidacao={} origem={} duracaoMs={}",
                textoHash,
                motivoPendencia.categoriaLog(),
                tentativasExecutadas,
                localidadePendenteSalva.getStatusValidacao(),
                localidadePendenteSalva.getOrigem(),
                calcularDuracaoMs(inicioResolucao)
        );

        return localidadePendenteSalva;
    }

    private long calcularDuracaoMs(long inicioNanos) {
        return (System.nanoTime() - inicioNanos) / 1_000_000;
    }
}
