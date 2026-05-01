package com.QueroTrabalhar.domain.entity.localidade;

import com.QueroTrabalhar.domain.enums.OrigemLocalidade;
import com.QueroTrabalhar.domain.enums.StatusValidacaoLocalidade;
import com.QueroTrabalhar.services.exceptions.BusinessRuleException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LocalidadePendenteTest {

    @Test
    void deveCriarLocalidadePendenteInformadaPeloUsuario() {
        // Arrange
        String textoOriginal = "São José dos Campos";
        String motivoPendencia = "Localidade aguardando validação manual";

        // Act
        LocalidadePendente localidadePendente =
                LocalidadePendente.criarPendenteInformadaPeloUsuario(textoOriginal, motivoPendencia);

        // Assert
        assertAll(
                () -> assertEquals("São José dos Campos", localidadePendente.getTextoOriginal()),
                () -> assertEquals("Localidade aguardando validação manual", localidadePendente.getMotivoPendencia()),
                () -> assertEquals(StatusValidacaoLocalidade.PENDENTE_VALIDACAO, localidadePendente.getStatusValidacao()),
                () -> assertEquals(OrigemLocalidade.USUARIO, localidadePendente.getOrigem()),
                () -> assertNotNull(localidadePendente.getCriadaEm()),
                () -> assertNotNull(localidadePendente.getAtualizadaEm())
        );
    }

    @Test
    void deveNormalizarTextoOriginalComTrim() {
        // Arrange
        String textoOriginal = "  Belo Horizonte  ";

        // Act
        LocalidadePendente localidadePendente =
                LocalidadePendente.criarPendenteInformadaPeloUsuario(textoOriginal, "Motivo válido");

        // Assert
        assertEquals("Belo Horizonte", localidadePendente.getTextoOriginal());
    }

    @Test
    void deveNormalizarMotivoPendenciaComTrim() {
        // Arrange
        String motivoPendencia = "  Não foi possível validar automaticamente a cidade informada.  ";

        // Act
        LocalidadePendente localidadePendente =
                LocalidadePendente.criarPendenteInformadaPeloUsuario("Campina Grande", motivoPendencia);

        // Assert
        assertEquals(
                "Não foi possível validar automaticamente a cidade informada.",
                localidadePendente.getMotivoPendencia()
        );
    }

    @Test
    void deveConverterMotivoPendenciaVazioParaNull() {
        // Arrange
        String motivoPendencia = "";

        // Act
        LocalidadePendente localidadePendente =
                LocalidadePendente.criarPendenteInformadaPeloUsuario("João Pessoa", motivoPendencia);

        // Assert
        assertNull(localidadePendente.getMotivoPendencia());
    }

    @Test
    void deveBloquearTextoOriginalNulo() {
        // Arrange / Act / Assert
        assertThrows(
                BusinessRuleException.class,
                () -> LocalidadePendente.criarPendenteInformadaPeloUsuario(null, "Motivo opcional")
        );
    }

    @Test
    void deveBloquearTextoOriginalVazio() {
        // Arrange / Act / Assert
        assertThrows(
                BusinessRuleException.class,
                () -> LocalidadePendente.criarPendenteInformadaPeloUsuario("   ", "Motivo opcional")
        );
    }

    @Test
    void deveBloquearStatusNuloNoConstrutor() {
        // Arrange / Act / Assert
        assertThrows(
                BusinessRuleException.class,
                () -> new LocalidadePendente("Recife", null, OrigemLocalidade.USUARIO, "Motivo opcional")
        );
    }

    @Test
    void deveBloquearOrigemNulaNoConstrutor() {
        // Arrange / Act / Assert
        assertThrows(
                BusinessRuleException.class,
                () -> new LocalidadePendente(
                        "Recife",
                        StatusValidacaoLocalidade.PENDENTE_VALIDACAO,
                        null,
                        "Motivo opcional"
                )
        );
    }

    @Test
    void deveAtualizarTextoOriginalComNormalizacao() {
        // Arrange
        LocalidadePendente localidadePendente = criarLocalidadePendenteValida();

        // Act
        localidadePendente.setTextoOriginal("  Mogi das Cruzes  ");

        // Assert
        assertEquals("Mogi das Cruzes", localidadePendente.getTextoOriginal());
    }

    @Test
    void deveBloquearSetTextoOriginalVazio() {
        // Arrange
        LocalidadePendente localidadePendente = criarLocalidadePendenteValida();

        // Act / Assert
        assertThrows(
                BusinessRuleException.class,
                () -> localidadePendente.setTextoOriginal("")
        );
    }

    private LocalidadePendente criarLocalidadePendenteValida() {
        return new LocalidadePendente(
                "Curitiba",
                StatusValidacaoLocalidade.PENDENTE_VALIDACAO,
                OrigemLocalidade.USUARIO,
                "Aguardando análise"
        );
    }
}
