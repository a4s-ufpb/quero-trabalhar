package com.QueroTrabalhar.services.localidade;

import com.QueroTrabalhar.domain.entity.localidade.LocalidadePendente;
import com.QueroTrabalhar.domain.enums.CampoLocalidadePendente;
import com.QueroTrabalhar.domain.enums.TipoRecursoLocalidadePendente;
import com.QueroTrabalhar.repository.LocalidadePendenteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Objects;

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
        return registrar(
                textoNormalizado,
                motivoPendencia,
                null,
                null,
                null,
                textoHash,
                tentativasExecutadas,
                inicioResolucao
        );
    }

    public LocalidadePendente registrar(
            String textoNormalizado,
            MotivoPendenciaLocalidade motivoPendencia,
            TipoRecursoLocalidadePendente tipoRecurso,
            Long recursoId,
            CampoLocalidadePendente campoAlvo,
            String textoHash,
            int tentativasExecutadas,
            long inicioResolucao
    ) {
        // A pendencia continua sendo um fallback tecnico interno e nao substitui a localidade validada.
        LocalidadePendente localidadePendente = LocalidadePendente.criarPendenteInformadaPeloUsuario(
                textoNormalizado,
                motivoPendencia.descricao(),
                tipoRecurso,
                recursoId,
                campoAlvo
        );
        LocalidadePendente localidadePendenteSalva = localidadePendenteRepository.save(localidadePendente);

        logger.warn(
                "event=localidade_pendente_criada textoHash={} motivoCategoria={} tentativasExecutadas={} statusValidacao={} origem={} tipoRecurso={} recursoId={} campoAlvo={} duracaoMs={}",
                textoHash,
                motivoPendencia.categoriaLog(),
                tentativasExecutadas,
                localidadePendenteSalva.getStatusValidacao(),
                localidadePendenteSalva.getOrigem(),
                localidadePendenteSalva.getTipoRecurso(),
                localidadePendenteSalva.getRecursoId(),
                localidadePendenteSalva.getCampoAlvo(),
                calcularDuracaoMs(inicioResolucao)
        );

        return localidadePendenteSalva;
    }

    public LocalidadePendente associarDonoGenerico(
            LocalidadePendente localidadePendente,
            TipoRecursoLocalidadePendente tipoRecurso,
            Long recursoId,
            CampoLocalidadePendente campoAlvo
    ) {
        if (localidadePendente.possuiDonoGenerico()
                && localidadePendente.getTipoRecurso() == tipoRecurso
                && Objects.equals(recursoId, localidadePendente.getRecursoId())
                && localidadePendente.getCampoAlvo() == campoAlvo) {
            return localidadePendente;
        }

        // Mantemos o FK legado nas entidades durante a migracao, mas a pendencia agora conhece
        // o recurso dono para preparar o reprocessamento tecnico futuro.
        localidadePendente.definirDonoGenerico(tipoRecurso, recursoId, campoAlvo);
        return localidadePendenteRepository.save(localidadePendente);
    }

    private long calcularDuracaoMs(long inicioNanos) {
        return (System.nanoTime() - inicioNanos) / 1_000_000;
    }
}
