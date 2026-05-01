package com.QueroTrabalhar.services.localidade;

import com.QueroTrabalhar.domain.entity.localidade.Cidade;
import com.QueroTrabalhar.domain.entity.localidade.Estado;
import com.QueroTrabalhar.domain.entity.localidade.LocalidadePendente;
import com.QueroTrabalhar.domain.entity.localidade.Pais;
import com.QueroTrabalhar.domain.enums.OrigemLocalidade;
import com.QueroTrabalhar.domain.enums.StatusValidacaoLocalidade;
import com.QueroTrabalhar.infrastructure.client.google.GoogleMapsClient;
import com.QueroTrabalhar.infrastructure.client.google.dto.AddressComponent;
import com.QueroTrabalhar.infrastructure.client.google.dto.GoogleGeocodeResponse;
import com.QueroTrabalhar.infrastructure.client.google.dto.GoogleResult;
import com.QueroTrabalhar.repository.CidadeRepository;
import com.QueroTrabalhar.repository.EstadoRepository;
import com.QueroTrabalhar.repository.LocalidadePendenteRepository;
import com.QueroTrabalhar.repository.PaisRepository;
import com.QueroTrabalhar.services.exceptions.BusinessRuleException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocalidadeResolucaoServiceTest {

    @Mock
    private CidadeRepository cidadeRepository;

    @Mock
    private EstadoRepository estadoRepository;

    @Mock
    private PaisRepository paisRepository;

    @Mock
    private LocalidadePendenteRepository localidadePendenteRepository;

    @Mock
    private GoogleMapsClient googleMapsClient;

    @InjectMocks
    private LocalidadeResolucaoService localidadeResolucaoService;

    @Test
    void deveResolverLocalidadePorCidadeExistenteNaBaseInterna() {
        // Arrange
        String textoLivre = "João Pessoa";
        Pais pais = criarPais(1L, "Brasil", "BR");
        Estado estado = criarEstado(10L, "Paraíba", "PB", pais);
        Cidade cidade = criarCidade(100L, "João Pessoa", estado);
        when(cidadeRepository.findAllByNomeIgnoreCase(textoLivre)).thenReturn(List.of(cidade));

        // Act
        ResultadoResolucaoLocalidade resultado = localidadeResolucaoService.resolver(textoLivre);

        // Assert
        assertAll(
                () -> assertTrue(resultado.resolvida()),
                () -> assertFalse(resultado.pendente()),
                () -> assertSame(pais, resultado.localidadeValidada().getPais()),
                () -> assertSame(estado, resultado.localidadeValidada().getEstado()),
                () -> assertSame(cidade, resultado.localidadeValidada().getCidade()),
                () -> assertNull(resultado.localidadePendente())
        );
        verifyNoInteractions(googleMapsClient);
        verify(localidadePendenteRepository, never()).save(any(LocalidadePendente.class));
    }

    @Test
    void deveResolverLocalidadePorEstadoExistenteNaBaseInterna() {
        // Arrange
        String textoLivre = "Paraíba";
        Pais pais = criarPais(1L, "Brasil", "BR");
        Estado estado = criarEstado(10L, "Paraíba", "PB", pais);
        when(cidadeRepository.findAllByNomeIgnoreCase(textoLivre)).thenReturn(List.of());
        when(estadoRepository.findAllByNomeIgnoreCase(textoLivre)).thenReturn(List.of(estado));

        // Act
        ResultadoResolucaoLocalidade resultado = localidadeResolucaoService.resolver(textoLivre);

        // Assert
        assertAll(
                () -> assertTrue(resultado.resolvida()),
                () -> assertFalse(resultado.pendente()),
                () -> assertSame(pais, resultado.localidadeValidada().getPais()),
                () -> assertSame(estado, resultado.localidadeValidada().getEstado()),
                () -> assertNull(resultado.localidadeValidada().getCidade()),
                () -> assertNull(resultado.localidadePendente())
        );
        verifyNoInteractions(googleMapsClient);
        verify(localidadePendenteRepository, never()).save(any(LocalidadePendente.class));
    }

    @Test
    void deveResolverLocalidadePorPaisExistenteNaBaseInterna() {
        // Arrange
        String textoLivre = "Brasil";
        Pais pais = criarPais(1L, "Brasil", "BR");
        when(cidadeRepository.findAllByNomeIgnoreCase(textoLivre)).thenReturn(List.of());
        when(estadoRepository.findAllByNomeIgnoreCase(textoLivre)).thenReturn(List.of());
        when(paisRepository.findFirstByNomeIgnoreCase(textoLivre)).thenReturn(Optional.of(pais));

        // Act
        ResultadoResolucaoLocalidade resultado = localidadeResolucaoService.resolver(textoLivre);

        // Assert
        assertAll(
                () -> assertTrue(resultado.resolvida()),
                () -> assertFalse(resultado.pendente()),
                () -> assertSame(pais, resultado.localidadeValidada().getPais()),
                () -> assertNull(resultado.localidadeValidada().getEstado()),
                () -> assertNull(resultado.localidadeValidada().getCidade()),
                () -> assertNull(resultado.localidadePendente())
        );
        verifyNoInteractions(googleMapsClient);
        verify(localidadePendenteRepository, never()).save(any(LocalidadePendente.class));
    }

    @Test
    void deveNaoEscolherCidadeQuandoHouverMaisDeUmaComMesmoNome() {
        // Arrange
        String textoLivre = "Santo André";
        Pais brasil = criarPais(1L, "Brasil", "BR");
        Estado saoPaulo = criarEstado(10L, "São Paulo", "SP", brasil);
        Estado bahia = criarEstado(20L, "Bahia", "BA", brasil);
        Cidade cidadeSp = criarCidade(100L, "Santo André", saoPaulo);
        Cidade cidadeBa = criarCidade(200L, "Santo André", bahia);
        mockSalvarLocalidadePendente();
        when(cidadeRepository.findAllByNomeIgnoreCase(textoLivre)).thenReturn(List.of(cidadeSp, cidadeBa));
        when(estadoRepository.findAllByNomeIgnoreCase(textoLivre)).thenReturn(List.of());
        when(paisRepository.findFirstByNomeIgnoreCase(textoLivre)).thenReturn(Optional.empty());
        when(googleMapsClient.buscarLugarComFiltro(textoLivre, null)).thenReturn(Optional.empty());

        // Act
        ResultadoResolucaoLocalidade resultado = localidadeResolucaoService.resolver(textoLivre);

        // Assert
        assertAll(
                () -> assertFalse(resultado.resolvida()),
                () -> assertTrue(resultado.pendente()),
                () -> assertNull(resultado.localidadeValidada()),
                () -> assertNotNull(resultado.localidadePendente()),
                () -> assertEquals("Santo André", resultado.localidadePendente().getTextoOriginal())
        );
        verify(googleMapsClient).buscarLugarComFiltro(textoLivre, null);
        verify(localidadePendenteRepository).save(any(LocalidadePendente.class));
        verify(paisRepository, never()).save(any(Pais.class));
        verify(estadoRepository, never()).save(any(Estado.class));
        verify(cidadeRepository, never()).save(any(Cidade.class));
    }

    @Test
    void deveCriarLocalidadePendenteQuandoTextoForNaoEncontradoEGoogleRetornarVazio() {
        // Arrange
        String textoLivre = "  São Tomé das Letras  ";
        String textoNormalizado = "São Tomé das Letras";
        mockBaseInternaNaoResolvida(textoNormalizado);
        mockSalvarLocalidadePendente();
        when(googleMapsClient.buscarLugarComFiltro(textoNormalizado, null)).thenReturn(Optional.empty());

        // Act
        ResultadoResolucaoLocalidade resultado = localidadeResolucaoService.resolver(textoLivre);

        // Assert
        ArgumentCaptor<LocalidadePendente> localidadeCaptor = ArgumentCaptor.forClass(LocalidadePendente.class);
        verify(localidadePendenteRepository).save(localidadeCaptor.capture());
        LocalidadePendente localidadeSalva = localidadeCaptor.getValue();

        assertAll(
                () -> assertFalse(resultado.resolvida()),
                () -> assertTrue(resultado.pendente()),
                () -> assertEquals(textoNormalizado, resultado.localidadePendente().getTextoOriginal()),
                () -> assertEquals(StatusValidacaoLocalidade.PENDENTE_VALIDACAO, resultado.localidadePendente().getStatusValidacao()),
                () -> assertEquals(OrigemLocalidade.USUARIO, resultado.localidadePendente().getOrigem()),
                () -> assertEquals(textoNormalizado, localidadeSalva.getTextoOriginal()),
                () -> assertEquals(StatusValidacaoLocalidade.PENDENTE_VALIDACAO, localidadeSalva.getStatusValidacao()),
                () -> assertEquals(OrigemLocalidade.USUARIO, localidadeSalva.getOrigem())
        );
        verify(googleMapsClient).buscarLugarComFiltro(textoNormalizado, null);
        verify(paisRepository, never()).save(any(Pais.class));
        verify(estadoRepository, never()).save(any(Estado.class));
        verify(cidadeRepository, never()).save(any(Cidade.class));
    }

    @Test
    void deveCriarLocalidadePendenteQuandoGoogleLancarExcecao() {
        // Arrange
        String textoLivre = "Nova Esperança";
        mockBaseInternaNaoResolvida(textoLivre);
        mockSalvarLocalidadePendente();
        when(googleMapsClient.buscarLugarComFiltro(textoLivre, null))
                .thenThrow(new RuntimeException("Timeout na integração externa"));

        // Act
        ResultadoResolucaoLocalidade resultado = localidadeResolucaoService.resolver(textoLivre);

        // Assert
        assertAll(
                () -> assertFalse(resultado.resolvida()),
                () -> assertTrue(resultado.pendente()),
                () -> assertEquals("Nova Esperança", resultado.localidadePendente().getTextoOriginal()),
                () -> assertNotNull(resultado.localidadePendente().getMotivoPendencia())
        );
        verify(localidadePendenteRepository).save(any(LocalidadePendente.class));
        verify(paisRepository, never()).save(any(Pais.class));
        verify(estadoRepository, never()).save(any(Estado.class));
        verify(cidadeRepository, never()).save(any(Cidade.class));
    }

    @Test
    void deveCriarLocalidadePendenteQuandoGoogleRetornarZeroResults() {
        // Arrange
        String textoLivre = "Vale Imaginário";
        mockBaseInternaNaoResolvida(textoLivre);
        mockSalvarLocalidadePendente();
        GoogleGeocodeResponse respostaGoogle = new GoogleGeocodeResponse(List.of(), "ZERO_RESULTS");
        when(googleMapsClient.buscarLugarComFiltro(textoLivre, null)).thenReturn(Optional.of(respostaGoogle));

        // Act
        ResultadoResolucaoLocalidade resultado = localidadeResolucaoService.resolver(textoLivre);

        // Assert
        assertAll(
                () -> assertFalse(resultado.resolvida()),
                () -> assertTrue(resultado.pendente()),
                () -> assertEquals("Vale Imaginário", resultado.localidadePendente().getTextoOriginal())
        );
        verify(localidadePendenteRepository).save(any(LocalidadePendente.class));
    }

    @Test
    void deveCriarLocalidadePendenteQuandoGoogleRetornarResultsVazio() {
        // Arrange
        String textoLivre = "Distrito Invisível";
        mockBaseInternaNaoResolvida(textoLivre);
        mockSalvarLocalidadePendente();
        GoogleGeocodeResponse respostaGoogle = new GoogleGeocodeResponse(List.of(), "OK");
        when(googleMapsClient.buscarLugarComFiltro(textoLivre, null)).thenReturn(Optional.of(respostaGoogle));

        // Act
        ResultadoResolucaoLocalidade resultado = localidadeResolucaoService.resolver(textoLivre);

        // Assert
        assertAll(
                () -> assertFalse(resultado.resolvida()),
                () -> assertTrue(resultado.pendente()),
                () -> assertEquals("Distrito Invisível", resultado.localidadePendente().getTextoOriginal())
        );
        verify(localidadePendenteRepository).save(any(LocalidadePendente.class));
    }

    @Test
    void deveResolverLocalidadeComGoogleQuandoRespostaForValida() {
        // Arrange
        String textoLivre = "Recife";
        Pais brasil = criarPais(1L, "Brasil", "BR");
        mockBaseInternaNaoResolvida(textoLivre);
        when(googleMapsClient.buscarLugarComFiltro(textoLivre, null))
                .thenReturn(Optional.of(criarRespostaGoogleValida("Brasil", "BR", "Pernambuco", "PE", "Recife")));
        when(paisRepository.findFirstBySiglaIgnoreCase("BR")).thenReturn(Optional.of(brasil));
        when(estadoRepository.findFirstByNomeIgnoreCaseAndPais("Pernambuco", brasil)).thenReturn(Optional.empty());
        when(estadoRepository.save(any(Estado.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(cidadeRepository.findFirstByNomeIgnoreCaseAndEstado(eq("Recife"), any(Estado.class)))
                .thenReturn(Optional.empty());
        when(cidadeRepository.save(any(Cidade.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ResultadoResolucaoLocalidade resultado = localidadeResolucaoService.resolver(textoLivre);

        // Assert
        assertAll(
                () -> assertTrue(resultado.resolvida()),
                () -> assertFalse(resultado.pendente()),
                () -> assertSame(brasil, resultado.localidadeValidada().getPais()),
                () -> assertEquals("Pernambuco", resultado.localidadeValidada().getEstado().getNome()),
                () -> assertEquals("PE", resultado.localidadeValidada().getEstado().getSigla()),
                () -> assertEquals("Recife", resultado.localidadeValidada().getCidade().getNome()),
                () -> assertNull(resultado.localidadePendente())
        );
        verify(googleMapsClient).buscarLugarComFiltro(textoLivre, null);
        verify(estadoRepository).save(any(Estado.class));
        verify(cidadeRepository).save(any(Cidade.class));
        verify(paisRepository, never()).save(any(Pais.class));
        verify(localidadePendenteRepository, never()).save(any(LocalidadePendente.class));
    }

    @Test
    void deveBloquearTextoLivreNulo() {
        // Arrange / Act / Assert
        assertThrows(BusinessRuleException.class, () -> localidadeResolucaoService.resolver(null));
        verifyNoInteractions(cidadeRepository, estadoRepository, paisRepository, localidadePendenteRepository, googleMapsClient);
    }

    @Test
    void deveBloquearTextoLivreVazio() {
        // Arrange / Act / Assert
        assertThrows(BusinessRuleException.class, () -> localidadeResolucaoService.resolver("   "));
        verifyNoInteractions(cidadeRepository, estadoRepository, paisRepository, localidadePendenteRepository, googleMapsClient);
    }

    private void mockBaseInternaNaoResolvida(String textoLivre) {
        when(cidadeRepository.findAllByNomeIgnoreCase(textoLivre)).thenReturn(List.of());
        when(estadoRepository.findAllByNomeIgnoreCase(textoLivre)).thenReturn(List.of());
        when(paisRepository.findFirstByNomeIgnoreCase(textoLivre)).thenReturn(Optional.empty());
    }

    private void mockSalvarLocalidadePendente() {
        when(localidadePendenteRepository.save(any(LocalidadePendente.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    private GoogleGeocodeResponse criarRespostaGoogleValida(
            String nomePais,
            String siglaPais,
            String nomeEstado,
            String siglaEstado,
            String nomeCidade
    ) {
        GoogleResult resultado = new GoogleResult(
                List.of(
                        criarComponente(nomeCidade, nomeCidade, "locality", "political"),
                        criarComponente(nomeEstado, siglaEstado, "administrative_area_level_1", "political"),
                        criarComponente(nomePais, siglaPais, "country", "political")
                ),
                nomeCidade + ", " + siglaEstado + ", " + nomePais
        );
        return new GoogleGeocodeResponse(List.of(resultado), "OK");
    }

    private AddressComponent criarComponente(String longName, String shortName, String... types) {
        return new AddressComponent(longName, shortName, List.of(types));
    }

    private Pais criarPais(Long id, String nome, String sigla) {
        Pais pais = new Pais(nome, sigla);
        ReflectionTestUtils.setField(pais, "id", id);
        return pais;
    }

    private Estado criarEstado(Long id, String nome, String sigla, Pais pais) {
        Estado estado = new Estado(nome, sigla, pais);
        ReflectionTestUtils.setField(estado, "id", id);
        return estado;
    }

    private Cidade criarCidade(Long id, String nome, Estado estado) {
        Cidade cidade = new Cidade(nome, estado);
        ReflectionTestUtils.setField(cidade, "id", id);
        return cidade;
    }
}
