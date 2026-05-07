package com.QueroTrabalhar.repository;

import com.QueroTrabalhar.domain.entity.localidade.LocalidadePendente;
import com.QueroTrabalhar.domain.enums.CampoLocalidadePendente;
import com.QueroTrabalhar.domain.enums.TipoRecursoLocalidadePendente;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class LocalidadePendenteRepositoryTest {

    @Autowired
    private LocalidadePendenteRepository localidadePendenteRepository;

    @Test
    void deveBuscarPendenciaPorTipoRecursoRecursoIdECampoAlvo() {
        LocalidadePendente pendenciaDaEmpresa = localidadePendenteRepository.saveAndFlush(
                LocalidadePendente.criarPendenteInformadaPeloUsuario(
                        "Vale Imaginario",
                        "Aguardando validacao",
                        TipoRecursoLocalidadePendente.EMPRESA,
                        10L,
                        CampoLocalidadePendente.LOCALIDADE
                )
        );
        localidadePendenteRepository.saveAndFlush(
                LocalidadePendente.criarPendenteInformadaPeloUsuario(
                        "Serra do Sol",
                        "Aguardando validacao",
                        TipoRecursoLocalidadePendente.OPORTUNIDADE_DE_EMPREGO,
                        25L,
                        CampoLocalidadePendente.LOCALIDADE
                )
        );

        Optional<LocalidadePendente> resultado =
                localidadePendenteRepository.findFirstByTipoRecursoAndRecursoIdAndCampoAlvo(
                        TipoRecursoLocalidadePendente.EMPRESA,
                        10L,
                        CampoLocalidadePendente.LOCALIDADE
                );

        assertTrue(resultado.isPresent());
        assertEquals(pendenciaDaEmpresa.getId(), resultado.get().getId());
        assertEquals("Vale Imaginario", resultado.get().getTextoOriginal());
    }
}
