package com.QueroTrabalhar.services;

import com.QueroTrabalhar.domain.dtos.localidade.CidadeResponseDTO;
import com.QueroTrabalhar.domain.dtos.localidade.EstadoResponseDTO;
import com.QueroTrabalhar.domain.dtos.localidade.PaisResponseDTO;
import com.QueroTrabalhar.domain.entity.localidade.Cidade;
import com.QueroTrabalhar.domain.entity.localidade.Estado;
import com.QueroTrabalhar.domain.entity.localidade.Pais;
import com.QueroTrabalhar.infrastructure.client.google.GoogleMapsClient;
import com.QueroTrabalhar.infrastructure.client.google.dto.AddressComponent;
import com.QueroTrabalhar.infrastructure.client.google.dto.GoogleGeocodeResponse;
import com.QueroTrabalhar.infrastructure.client.google.dto.GoogleResult;
import com.QueroTrabalhar.repository.CidadeRepository;
import com.QueroTrabalhar.repository.EstadoRepository;
import com.QueroTrabalhar.repository.PaisRepository;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocalidadeServiceTest {

    @Mock
    private PaisRepository paisRepository;

    @Mock
    private EstadoRepository estadoRepository;

    @Mock
    private CidadeRepository cidadeRepository;

    @Mock
    private GoogleMapsClient googleMapsClient;

    @InjectMocks
    private LocalidadeService localidadeService;

    @Test
    void deveRetornarPaisesExistentesLocalmente() {
        String termoBusca = "bra";
        Pais brasil = criarPais(1L, "Brasil", "BR");
        Pais brunei = criarPais(2L, "Brunei", "BN");
        when(paisRepository.findByNomeContainingIgnoreCase(termoBusca)).thenReturn(List.of(brasil, brunei));

        List<PaisResponseDTO> resultado = localidadeService.buscarPais(termoBusca);

        assertAll(
                () -> assertEquals(2, resultado.size()),
                () -> assertEquals(1L, resultado.getFirst().id()),
                () -> assertEquals("Brasil", resultado.getFirst().nome()),
                () -> assertEquals("BR", resultado.getFirst().sigla()),
                () -> assertEquals(2L, resultado.get(1).id()),
                () -> assertEquals("Brunei", resultado.get(1).nome()),
                () -> assertEquals("BN", resultado.get(1).sigla())
        );
        verify(paisRepository).findByNomeContainingIgnoreCase(termoBusca);
        verifyNoInteractions(estadoRepository, cidadeRepository, googleMapsClient);
    }

    @Test
    void deveRetornarEstadosExistentesPorPais() {
        Long paisId = 1L;
        String termoBusca = "per";
        Pais brasil = criarPais(paisId, "Brasil", "BR");
        Estado pernambuco = criarEstado(10L, "Pernambuco", "PE", brasil);
        when(paisRepository.findById(paisId)).thenReturn(Optional.of(brasil));
        when(estadoRepository.findByPaisAndNomeContainingIgnoreCase(brasil, termoBusca))
                .thenReturn(List.of(pernambuco));

        List<EstadoResponseDTO> resultado = localidadeService.buscarEstado(paisId, termoBusca);

        assertAll(
                () -> assertEquals(1, resultado.size()),
                () -> assertEquals(10L, resultado.getFirst().id()),
                () -> assertEquals("Pernambuco", resultado.getFirst().nome()),
                () -> assertEquals("PE", resultado.getFirst().sigla()),
                () -> assertEquals(paisId, resultado.getFirst().pais_id())
        );
        verify(paisRepository).findById(paisId);
        verify(estadoRepository).findByPaisAndNomeContainingIgnoreCase(brasil, termoBusca);
        verify(estadoRepository, never()).findByNomeAndPais(any(String.class), any(Pais.class));
        verify(estadoRepository, never()).save(any(Estado.class));
        verifyNoInteractions(cidadeRepository, googleMapsClient);
    }

    @Test
    void deveLancarExcecaoQuandoPaisNaoExistirAoBuscarEstado() {
        Long paisId = 99L;
        when(paisRepository.findById(paisId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> localidadeService.buscarEstado(paisId, "per")
        );

        assertNotNull(exception);
        verify(paisRepository).findById(paisId);
        verifyNoInteractions(estadoRepository, cidadeRepository, googleMapsClient);
    }

    @Test
    void deveBuscarEstadoNoGoogleECriarNovoEstadoQuandoNaoExistirLocalmente() {
        Long paisId = 1L;
        String termoBusca = "pernambuco";
        Pais brasil = criarPais(paisId, "Brasil", "BR");
        when(paisRepository.findById(paisId)).thenReturn(Optional.of(brasil));
        when(estadoRepository.findByPaisAndNomeContainingIgnoreCase(brasil, termoBusca)).thenReturn(List.of());
        when(googleMapsClient.buscarLugarComFiltro(termoBusca, "country:BR"))
                .thenReturn(Optional.of(criarRespostaGoogle(
                        criarComponente("Pernambuco", "PE", "administrative_area_level_1", "political"),
                        criarComponente("Brasil", "BR", "country", "political")
                )));
        when(estadoRepository.findByNomeAndPais("Pernambuco", brasil)).thenReturn(Optional.empty());
        when(estadoRepository.save(any(Estado.class))).thenAnswer(invocation -> {
            Estado estado = invocation.getArgument(0);
            ReflectionTestUtils.setField(estado, "id", 10L);
            return estado;
        });

        List<EstadoResponseDTO> resultado = localidadeService.buscarEstado(paisId, termoBusca);

        ArgumentCaptor<Estado> estadoCaptor = ArgumentCaptor.forClass(Estado.class);
        verify(estadoRepository).save(estadoCaptor.capture());
        Estado estadoSalvo = estadoCaptor.getValue();

        assertAll(
                () -> assertEquals(1, resultado.size()),
                () -> assertEquals(10L, resultado.getFirst().id()),
                () -> assertEquals("Pernambuco", resultado.getFirst().nome()),
                () -> assertEquals("PE", resultado.getFirst().sigla()),
                () -> assertEquals(paisId, resultado.getFirst().pais_id()),
                () -> assertEquals("Pernambuco", estadoSalvo.getNome()),
                () -> assertEquals("PE", estadoSalvo.getSigla()),
                () -> assertEquals(brasil, estadoSalvo.getPais())
        );
        verify(googleMapsClient).buscarLugarComFiltro(termoBusca, "country:BR");
        verifyNoInteractions(cidadeRepository);
    }

    @Test
    void deveReaproveitarEstadoExistenteQuandoFallbackGoogleEncontrarMesmoEstado() {
        Long paisId = 1L;
        String termoBusca = "pernambuco";
        Pais brasil = criarPais(paisId, "Brasil", "BR");
        Estado pernambuco = criarEstado(10L, "Pernambuco", "PE", brasil);
        when(paisRepository.findById(paisId)).thenReturn(Optional.of(brasil));
        when(estadoRepository.findByPaisAndNomeContainingIgnoreCase(brasil, termoBusca)).thenReturn(List.of());
        when(googleMapsClient.buscarLugarComFiltro(termoBusca, "country:BR"))
                .thenReturn(Optional.of(criarRespostaGoogle(
                        criarComponente("Pernambuco", "PE", "administrative_area_level_1", "political"),
                        criarComponente("Brasil", "BR", "country", "political")
                )));
        when(estadoRepository.findByNomeAndPais("Pernambuco", brasil)).thenReturn(Optional.of(pernambuco));

        List<EstadoResponseDTO> resultado = localidadeService.buscarEstado(paisId, termoBusca);

        assertAll(
                () -> assertEquals(1, resultado.size()),
                () -> assertEquals(10L, resultado.getFirst().id()),
                () -> assertEquals("Pernambuco", resultado.getFirst().nome()),
                () -> assertEquals("PE", resultado.getFirst().sigla()),
                () -> assertEquals(paisId, resultado.getFirst().pais_id())
        );
        verify(googleMapsClient).buscarLugarComFiltro(termoBusca, "country:BR");
        verify(estadoRepository, never()).save(any(Estado.class));
        verifyNoInteractions(cidadeRepository);
    }

    @Test
    void deveRetornarListaVaziaQuandoFallbackGoogleDeEstadoRetornarOptionalEmpty() {
        Long paisId = 1L;
        String termoBusca = "acre";
        Pais brasil = criarPais(paisId, "Brasil", "BR");
        when(paisRepository.findById(paisId)).thenReturn(Optional.of(brasil));
        when(estadoRepository.findByPaisAndNomeContainingIgnoreCase(brasil, termoBusca)).thenReturn(List.of());
        when(googleMapsClient.buscarLugarComFiltro(termoBusca, "country:BR")).thenReturn(Optional.empty());

        List<EstadoResponseDTO> resultado = localidadeService.buscarEstado(paisId, termoBusca);

        assertTrue(resultado.isEmpty());
        verify(googleMapsClient).buscarLugarComFiltro(termoBusca, "country:BR");
        verify(estadoRepository, never()).findByNomeAndPais(any(String.class), any(Pais.class));
        verify(estadoRepository, never()).save(any(Estado.class));
        verifyNoInteractions(cidadeRepository);
    }

    @Test
    void deveRetornarListaVaziaQuandoFallbackGoogleDeEstadoNaoTrouxerComponenteDeEstado() {
        Long paisId = 1L;
        String termoBusca = "interior";
        Pais brasil = criarPais(paisId, "Brasil", "BR");
        when(paisRepository.findById(paisId)).thenReturn(Optional.of(brasil));
        when(estadoRepository.findByPaisAndNomeContainingIgnoreCase(brasil, termoBusca)).thenReturn(List.of());
        when(googleMapsClient.buscarLugarComFiltro(termoBusca, "country:BR"))
                .thenReturn(Optional.of(criarRespostaGoogle(
                        criarComponente("Brasil", "BR", "country", "political")
                )));

        List<EstadoResponseDTO> resultado = localidadeService.buscarEstado(paisId, termoBusca);

        assertTrue(resultado.isEmpty());
        verify(googleMapsClient).buscarLugarComFiltro(termoBusca, "country:BR");
        verify(estadoRepository, never()).findByNomeAndPais(any(String.class), any(Pais.class));
        verify(estadoRepository, never()).save(any(Estado.class));
        verifyNoInteractions(cidadeRepository);
    }

    @Test
    void deveRetornarCidadesExistentesPorEstado() {
        Long estadoId = 10L;
        String termoBusca = "rec";
        Pais brasil = criarPais(1L, "Brasil", "BR");
        Estado pernambuco = criarEstado(estadoId, "Pernambuco", "PE", brasil);
        Cidade recife = criarCidade(100L, "Recife", pernambuco);
        when(estadoRepository.findById(estadoId)).thenReturn(Optional.of(pernambuco));
        when(cidadeRepository.findByEstadoAndNomeContainingIgnoreCase(pernambuco, termoBusca))
                .thenReturn(List.of(recife));

        List<CidadeResponseDTO> resultado = localidadeService.buscarCidade(estadoId, termoBusca);

        assertAll(
                () -> assertEquals(1, resultado.size()),
                () -> assertEquals(100L, resultado.getFirst().id()),
                () -> assertEquals("Recife", resultado.getFirst().nome()),
                () -> assertEquals(estadoId, resultado.getFirst().estado())
        );
        verify(estadoRepository).findById(estadoId);
        verify(cidadeRepository).findByEstadoAndNomeContainingIgnoreCase(pernambuco, termoBusca);
        verify(cidadeRepository, never()).findByNomeAndEstado(any(String.class), any(Estado.class));
        verify(cidadeRepository, never()).save(any(Cidade.class));
        verifyNoInteractions(googleMapsClient, paisRepository);
    }

    @Test
    void deveLancarExcecaoQuandoEstadoNaoExistirAoBuscarCidade() {
        Long estadoId = 99L;
        when(estadoRepository.findById(estadoId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> localidadeService.buscarCidade(estadoId, "rec")
        );

        assertNotNull(exception);
        verify(estadoRepository).findById(estadoId);
        verifyNoInteractions(cidadeRepository, googleMapsClient, paisRepository);
    }

    @Test
    void deveBuscarCidadeNoGoogleECriarNovaCidadeQuandoNaoExistirLocalmente() {
        Long estadoId = 10L;
        String termoBusca = "recife";
        Pais brasil = criarPais(1L, "Brasil", "BR");
        Estado pernambuco = criarEstado(estadoId, "Pernambuco", "PE", brasil);
        when(estadoRepository.findById(estadoId)).thenReturn(Optional.of(pernambuco));
        when(cidadeRepository.findByEstadoAndNomeContainingIgnoreCase(pernambuco, termoBusca)).thenReturn(List.of());
        when(googleMapsClient.buscarLugarComFiltro(termoBusca, "country:BR|administrative_area:PE"))
                .thenReturn(Optional.of(criarRespostaGoogle(
                        criarComponente("Recife", "Recife", "locality", "political"),
                        criarComponente("Pernambuco", "PE", "administrative_area_level_1", "political"),
                        criarComponente("Brasil", "BR", "country", "political")
                )));
        when(cidadeRepository.findByNomeAndEstado("Recife", pernambuco)).thenReturn(Optional.empty());
        when(cidadeRepository.save(any(Cidade.class))).thenAnswer(invocation -> {
            Cidade cidade = invocation.getArgument(0);
            ReflectionTestUtils.setField(cidade, "id", 100L);
            return cidade;
        });

        List<CidadeResponseDTO> resultado = localidadeService.buscarCidade(estadoId, termoBusca);

        ArgumentCaptor<Cidade> cidadeCaptor = ArgumentCaptor.forClass(Cidade.class);
        verify(cidadeRepository).save(cidadeCaptor.capture());
        Cidade cidadeSalva = cidadeCaptor.getValue();

        assertAll(
                () -> assertEquals(1, resultado.size()),
                () -> assertEquals(100L, resultado.getFirst().id()),
                () -> assertEquals("Recife", resultado.getFirst().nome()),
                () -> assertEquals(estadoId, resultado.getFirst().estado()),
                () -> assertEquals("Recife", cidadeSalva.getNome()),
                () -> assertEquals(pernambuco, cidadeSalva.getEstado())
        );
        verify(googleMapsClient).buscarLugarComFiltro(termoBusca, "country:BR|administrative_area:PE");
        verifyNoInteractions(paisRepository);
    }

    @Test
    void deveReaproveitarCidadeExistenteQuandoFallbackGoogleEncontrarMesmaCidade() {
        Long estadoId = 10L;
        String termoBusca = "recife";
        Pais brasil = criarPais(1L, "Brasil", "BR");
        Estado pernambuco = criarEstado(estadoId, "Pernambuco", "PE", brasil);
        Cidade recife = criarCidade(100L, "Recife", pernambuco);
        when(estadoRepository.findById(estadoId)).thenReturn(Optional.of(pernambuco));
        when(cidadeRepository.findByEstadoAndNomeContainingIgnoreCase(pernambuco, termoBusca)).thenReturn(List.of());
        when(googleMapsClient.buscarLugarComFiltro(termoBusca, "country:BR|administrative_area:PE"))
                .thenReturn(Optional.of(criarRespostaGoogle(
                        criarComponente("Recife", "Recife", "locality", "political"),
                        criarComponente("Pernambuco", "PE", "administrative_area_level_1", "political"),
                        criarComponente("Brasil", "BR", "country", "political")
                )));
        when(cidadeRepository.findByNomeAndEstado("Recife", pernambuco)).thenReturn(Optional.of(recife));

        List<CidadeResponseDTO> resultado = localidadeService.buscarCidade(estadoId, termoBusca);

        assertAll(
                () -> assertEquals(1, resultado.size()),
                () -> assertEquals(100L, resultado.getFirst().id()),
                () -> assertEquals("Recife", resultado.getFirst().nome()),
                () -> assertEquals(estadoId, resultado.getFirst().estado())
        );
        verify(googleMapsClient).buscarLugarComFiltro(termoBusca, "country:BR|administrative_area:PE");
        verify(cidadeRepository, never()).save(any(Cidade.class));
        verifyNoInteractions(paisRepository);
    }

    @Test
    void deveCriarCidadeUsandoAdministrativeAreaLevel2QuandoLocalityNaoVierNaResposta() {
        Long estadoId = 10L;
        String termoBusca = "caruaru";
        Pais brasil = criarPais(1L, "Brasil", "BR");
        Estado pernambuco = criarEstado(estadoId, "Pernambuco", "PE", brasil);
        when(estadoRepository.findById(estadoId)).thenReturn(Optional.of(pernambuco));
        when(cidadeRepository.findByEstadoAndNomeContainingIgnoreCase(pernambuco, termoBusca)).thenReturn(List.of());
        when(googleMapsClient.buscarLugarComFiltro(termoBusca, "country:BR|administrative_area:PE"))
                .thenReturn(Optional.of(criarRespostaGoogle(
                        criarComponente("Caruaru", "Caruaru", "administrative_area_level_2", "political"),
                        criarComponente("Pernambuco", "PE", "administrative_area_level_1", "political"),
                        criarComponente("Brasil", "BR", "country", "political")
                )));
        when(cidadeRepository.findByNomeAndEstado("Caruaru", pernambuco)).thenReturn(Optional.empty());
        when(cidadeRepository.save(any(Cidade.class))).thenAnswer(invocation -> {
            Cidade cidade = invocation.getArgument(0);
            ReflectionTestUtils.setField(cidade, "id", 101L);
            return cidade;
        });

        List<CidadeResponseDTO> resultado = localidadeService.buscarCidade(estadoId, termoBusca);

        assertAll(
                () -> assertEquals(1, resultado.size()),
                () -> assertEquals(101L, resultado.getFirst().id()),
                () -> assertEquals("Caruaru", resultado.getFirst().nome()),
                () -> assertEquals(estadoId, resultado.getFirst().estado())
        );
        verify(cidadeRepository).save(any(Cidade.class));
        verifyNoInteractions(paisRepository);
    }

    @Test
    void deveRetornarListaVaziaQuandoFallbackGoogleDeCidadeRetornarZeroResults() {
        Long estadoId = 10L;
        String termoBusca = "lugar-inexistente";
        Pais brasil = criarPais(1L, "Brasil", "BR");
        Estado pernambuco = criarEstado(estadoId, "Pernambuco", "PE", brasil);
        when(estadoRepository.findById(estadoId)).thenReturn(Optional.of(pernambuco));
        when(cidadeRepository.findByEstadoAndNomeContainingIgnoreCase(pernambuco, termoBusca)).thenReturn(List.of());
        when(googleMapsClient.buscarLugarComFiltro(termoBusca, "country:BR|administrative_area:PE"))
                .thenReturn(Optional.of(new GoogleGeocodeResponse(List.of(), "ZERO_RESULTS")));

        List<CidadeResponseDTO> resultado = localidadeService.buscarCidade(estadoId, termoBusca);

        assertTrue(resultado.isEmpty());
        verify(googleMapsClient).buscarLugarComFiltro(termoBusca, "country:BR|administrative_area:PE");
        verify(cidadeRepository, never()).findByNomeAndEstado(any(String.class), any(Estado.class));
        verify(cidadeRepository, never()).save(any(Cidade.class));
        verifyNoInteractions(paisRepository);
    }

    @Test
    void deveRetornarListaVaziaQuandoFallbackGoogleDeCidadeNaoTrouxerComponenteUtil() {
        Long estadoId = 10L;
        String termoBusca = "interior";
        Pais brasil = criarPais(1L, "Brasil", "BR");
        Estado pernambuco = criarEstado(estadoId, "Pernambuco", "PE", brasil);
        when(estadoRepository.findById(estadoId)).thenReturn(Optional.of(pernambuco));
        when(cidadeRepository.findByEstadoAndNomeContainingIgnoreCase(pernambuco, termoBusca)).thenReturn(List.of());
        when(googleMapsClient.buscarLugarComFiltro(termoBusca, "country:BR|administrative_area:PE"))
                .thenReturn(Optional.of(criarRespostaGoogle(
                        criarComponente("Pernambuco", "PE", "administrative_area_level_1", "political"),
                        criarComponente("Brasil", "BR", "country", "political")
                )));

        List<CidadeResponseDTO> resultado = localidadeService.buscarCidade(estadoId, termoBusca);

        assertTrue(resultado.isEmpty());
        verify(googleMapsClient).buscarLugarComFiltro(termoBusca, "country:BR|administrative_area:PE");
        verify(cidadeRepository, never()).findByNomeAndEstado(any(String.class), any(Estado.class));
        verify(cidadeRepository, never()).save(any(Cidade.class));
        verifyNoInteractions(paisRepository);
    }

    private GoogleGeocodeResponse criarRespostaGoogle(AddressComponent... componentes) {
        return new GoogleGeocodeResponse(
                List.of(new GoogleResult(List.of(componentes), "Endereco formatado")),
                "OK"
        );
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
