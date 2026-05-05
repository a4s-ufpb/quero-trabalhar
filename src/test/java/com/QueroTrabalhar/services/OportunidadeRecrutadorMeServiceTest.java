package com.QueroTrabalhar.services;

import com.QueroTrabalhar.domain.dtos.oportunidadeDeEmprego.OportunidadeDeEmpregoFilterDTO;
import com.QueroTrabalhar.domain.dtos.oportunidadeDeEmprego.OportunidadeDeEmpregoResponseDTO;
import com.QueroTrabalhar.domain.entity.Empresa;
import com.QueroTrabalhar.domain.entity.OportunidadeDeEmprego;
import com.QueroTrabalhar.domain.entity.PerfilRecrutador;
import com.QueroTrabalhar.domain.entity.TipoDeEmprego;
import com.QueroTrabalhar.domain.entity.Usuario;
import com.QueroTrabalhar.domain.entity.localidade.Localidade;
import com.QueroTrabalhar.domain.entity.localidade.Pais;
import com.QueroTrabalhar.domain.enums.Modalidade;
import com.QueroTrabalhar.domain.enums.StatusLocalidadeFiltro;
import com.QueroTrabalhar.repository.CidadeRepository;
import com.QueroTrabalhar.repository.EstadoRepository;
import com.QueroTrabalhar.repository.OportunidadeDeEmpregoRepository;
import com.QueroTrabalhar.repository.PaisRepository;
import com.QueroTrabalhar.repository.TipoDeEmpregoRepository;
import com.QueroTrabalhar.services.exceptions.BusinessRuleException;
import com.QueroTrabalhar.services.exceptions.ObjectNotFoundException;
import com.QueroTrabalhar.services.localidade.LocalidadeResolucaoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OportunidadeRecrutadorMeServiceTest {

    @Mock
    private OportunidadeDeEmpregoRepository oportunidadeDeEmpregoRepository;

    @Mock
    private TipoDeEmpregoRepository tipoDeEmpregoRepository;

    @Mock
    private PaisRepository paisRepository;

    @Mock
    private EstadoRepository estadoRepository;

    @Mock
    private CidadeRepository cidadeRepository;

    @Mock
    private LocalidadeResolucaoService localidadeResolucaoService;

    @Mock
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @InjectMocks
    private OportunidadeDeEmpregoService oportunidadeDeEmpregoService;

    @Test
    void deveListarOportunidadesComPaginacaoEFiltros() {
        PerfilRecrutador recrutador = criarPerfilRecrutadorAutenticado(21L, "Mariana Alves", "Empresa Legada");
        TipoDeEmprego tipoDeEmprego = criarTipoDeEmpregoAprovado(5L, "Desenvolvedor Backend");
        Pais pais = criarPais(1L, "Brasil", "BR");
        Empresa empresa = criarEmpresa(80L, "Empresa Nuvem");
        Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "id"));
        OportunidadeDeEmpregoFilterDTO filtro = new OportunidadeDeEmpregoFilterDTO(
                "Spring",
                tipoDeEmprego.getId(),
                empresa.getId(),
                recrutador.getId(),
                pais.getId(),
                null,
                null,
                StatusLocalidadeFiltro.VALIDADA,
                Modalidade.REMOTO
        );
        OportunidadeDeEmprego oportunidadePessoal = criarOportunidade(
                501L,
                "Java 21 e Spring Boot",
                Modalidade.REMOTO,
                tipoDeEmprego,
                recrutador,
                null,
                pais
        );
        OportunidadeDeEmprego oportunidadeEmEmpresa = criarOportunidade(
                502L,
                "Arquitetura de microsservicos",
                Modalidade.HIBRIDO,
                tipoDeEmprego,
                recrutador,
                empresa,
                pais
        );

        when(oportunidadeDeEmpregoRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(
                new PageImpl<>(List.of(oportunidadePessoal, oportunidadeEmEmpresa), pageable, 2)
        );

        Page<OportunidadeDeEmpregoResponseDTO> resposta =
                oportunidadeDeEmpregoService.listarOportunidadesDeEmprego(filtro, pageable);

        assertEquals(2, resposta.getTotalElements());
        assertEquals(1, resposta.getTotalPages());
        assertEquals(2, resposta.getContent().size());
        assertInstanceOf(OportunidadeDeEmpregoResponseDTO.class, resposta.getContent().get(0));
        assertNotSame(oportunidadePessoal, resposta.getContent().get(0));

        OportunidadeDeEmpregoResponseDTO primeiraOportunidade = resposta.getContent().get(0);
        OportunidadeDeEmpregoResponseDTO segundaOportunidade = resposta.getContent().get(1);

        assertAll(
                () -> assertEquals(501L, primeiraOportunidade.id()),
                () -> assertEquals("Java 21 e Spring Boot", primeiraOportunidade.descricao()),
                () -> assertEquals(21L, primeiraOportunidade.recrutadorId()),
                () -> assertNull(primeiraOportunidade.empresaId()),
                () -> assertEquals(502L, segundaOportunidade.id()),
                () -> assertEquals("Arquitetura de microsservicos", segundaOportunidade.descricao()),
                () -> assertEquals(80L, segundaOportunidade.empresaId()),
                () -> assertEquals("Empresa Nuvem", segundaOportunidade.empresaNome())
        );
        verify(oportunidadeDeEmpregoRepository).findAll(any(Specification.class), eq(pageable));
        verifyNoInteractions(
                usuarioAutenticadoService,
                tipoDeEmpregoRepository,
                paisRepository,
                estadoRepository,
                cidadeRepository,
                localidadeResolucaoService
        );
    }

    @Test
    void deveBuscarOportunidadePorIdComSucesso() {
        PerfilRecrutador recrutador = criarPerfilRecrutadorAutenticado(31L, "Paula Mendes", "Empresa Legada");
        TipoDeEmprego tipoDeEmprego = criarTipoDeEmpregoAprovado(6L, "Tech Lead");
        Pais pais = criarPais(1L, "Brasil", "BR");
        Empresa empresa = criarEmpresa(81L, "Empresa Plataforma");
        OportunidadeDeEmprego oportunidade = criarOportunidade(
                601L,
                "Lideranca tecnica com Java",
                Modalidade.PRESENCIAL,
                tipoDeEmprego,
                recrutador,
                empresa,
                pais
        );

        when(oportunidadeDeEmpregoRepository.findById(601L)).thenReturn(Optional.of(oportunidade));

        OportunidadeDeEmpregoResponseDTO resposta = oportunidadeDeEmpregoService.buscarPorId(601L);

        assertNotSame(oportunidade, resposta);
        assertAll(
                () -> assertEquals(601L, resposta.id()),
                () -> assertEquals("Lideranca tecnica com Java", resposta.descricao()),
                () -> assertEquals(31L, resposta.recrutadorId()),
                () -> assertEquals("Paula Mendes", resposta.recrutadorNome()),
                () -> assertEquals(81L, resposta.empresaId()),
                () -> assertEquals("Empresa Plataforma", resposta.empresaNome())
        );
        verify(oportunidadeDeEmpregoRepository).findById(601L);
        verifyNoInteractions(
                usuarioAutenticadoService,
                tipoDeEmpregoRepository,
                paisRepository,
                estadoRepository,
                cidadeRepository,
                localidadeResolucaoService
        );
    }

    @Test
    void deveLancarObjectNotFoundExceptionQuandoOportunidadeNaoExistir() {
        when(oportunidadeDeEmpregoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> oportunidadeDeEmpregoService.buscarPorId(999L));

        verify(oportunidadeDeEmpregoRepository).findById(999L);
        verifyNoInteractions(
                usuarioAutenticadoService,
                tipoDeEmpregoRepository,
                paisRepository,
                estadoRepository,
                cidadeRepository,
                localidadeResolucaoService
        );
    }

    @Test
    void deveListarOportunidadesDoRecrutadorAutenticado() {
        PerfilRecrutador perfilRecrutador =
                criarPerfilRecrutadorAutenticado(15L, "Camila Souza", "Empresa Legada");
        TipoDeEmprego tipoDeEmprego = criarTipoDeEmpregoAprovado(3L, "Desenvolvedor Java");
        Pais pais = criarPais(1L, "Brasil", "BR");
        OportunidadeDeEmprego oportunidadePessoal = criarOportunidade(
                101L,
                "Backend Java 21",
                Modalidade.REMOTO,
                tipoDeEmprego,
                perfilRecrutador,
                null,
                pais
        );
        Empresa empresa = criarEmpresa(50L, "Empresa Ágil");
        OportunidadeDeEmprego oportunidadeEmEmpresa = criarOportunidade(
                102L,
                "Microsserviços com Spring Boot",
                Modalidade.HIBRIDO,
                tipoDeEmprego,
                perfilRecrutador,
                empresa,
                pais
        );

        when(usuarioAutenticadoService.obterPerfilRecrutadorAutenticado()).thenReturn(perfilRecrutador);
        when(oportunidadeDeEmpregoRepository.findByPerfilRecrutadorId(15L))
                .thenReturn(List.of(oportunidadePessoal, oportunidadeEmEmpresa));

        List<OportunidadeDeEmpregoResponseDTO> resposta =
                oportunidadeDeEmpregoService.listarOportunidadesDoRecrutadorAutenticado();

        assertEquals(2, resposta.size());
        assertAll(
                () -> assertEquals(101L, resposta.get(0).id()),
                () -> assertEquals("Backend Java 21", resposta.get(0).descricao()),
                () -> assertEquals(15L, resposta.get(0).recrutadorId()),
                () -> assertEquals("Camila Souza", resposta.get(0).recrutadorNome()),
                () -> assertEquals(102L, resposta.get(1).id()),
                () -> assertEquals("Microsserviços com Spring Boot", resposta.get(1).descricao()),
                () -> assertEquals(15L, resposta.get(1).recrutadorId()),
                () -> assertEquals("Camila Souza", resposta.get(1).recrutadorNome())
        );
        verify(usuarioAutenticadoService).obterPerfilRecrutadorAutenticado();
        verify(oportunidadeDeEmpregoRepository).findByPerfilRecrutadorId(15L);
        verifyNoInteractions(tipoDeEmpregoRepository, paisRepository, estadoRepository, cidadeRepository);
    }

    @Test
    void deveIncluirOportunidadesPessoaisComEmpresaNull() {
        PerfilRecrutador perfilRecrutador =
                criarPerfilRecrutadorAutenticado(15L, "Ana Beatriz", "Empresa Legada");
        TipoDeEmprego tipoDeEmprego = criarTipoDeEmpregoAprovado(3L, "Desenvolvedor Java");
        Pais pais = criarPais(1L, "Brasil", "BR");
        OportunidadeDeEmprego oportunidadePessoal = criarOportunidade(
                201L,
                "APIs REST com Spring",
                Modalidade.REMOTO,
                tipoDeEmprego,
                perfilRecrutador,
                null,
                pais
        );

        when(usuarioAutenticadoService.obterPerfilRecrutadorAutenticado()).thenReturn(perfilRecrutador);
        when(oportunidadeDeEmpregoRepository.findByPerfilRecrutadorId(15L))
                .thenReturn(List.of(oportunidadePessoal));

        List<OportunidadeDeEmpregoResponseDTO> resposta =
                oportunidadeDeEmpregoService.listarOportunidadesDoRecrutadorAutenticado();

        assertAll(
                () -> assertEquals(201L, resposta.get(0).id()),
                () -> assertNull(resposta.get(0).empresaId()),
                () -> assertNull(resposta.get(0).empresaNome())
        );
    }

    @Test
    void deveIncluirOportunidadesPublicadasEmNomeDeEmpresa() {
        PerfilRecrutador perfilRecrutador =
                criarPerfilRecrutadorAutenticado(15L, "Pedro Nogueira", "Empresa Legada");
        TipoDeEmprego tipoDeEmprego = criarTipoDeEmpregoAprovado(3L, "Desenvolvedor Java");
        Pais pais = criarPais(1L, "Brasil", "BR");
        Empresa empresa = criarEmpresa(70L, "Inovação Paraíba");
        OportunidadeDeEmprego oportunidadeEmEmpresa = criarOportunidade(
                301L,
                "Liderança técnica em plataforma",
                Modalidade.PRESENCIAL,
                tipoDeEmprego,
                perfilRecrutador,
                empresa,
                pais
        );

        when(usuarioAutenticadoService.obterPerfilRecrutadorAutenticado()).thenReturn(perfilRecrutador);
        when(oportunidadeDeEmpregoRepository.findByPerfilRecrutadorId(15L))
                .thenReturn(List.of(oportunidadeEmEmpresa));

        List<OportunidadeDeEmpregoResponseDTO> resposta =
                oportunidadeDeEmpregoService.listarOportunidadesDoRecrutadorAutenticado();

        assertAll(
                () -> assertEquals(301L, resposta.get(0).id()),
                () -> assertEquals(70L, resposta.get(0).empresaId()),
                () -> assertEquals("Inovação Paraíba", resposta.get(0).empresaNome())
        );
    }

    @Test
    void deveRetornarOportunidadeDeEmpregoResponseDTOSemExporEntidade() {
        PerfilRecrutador perfilRecrutador =
                criarPerfilRecrutadorAutenticado(15L, "Lucas Martins", "Empresa Legada");
        TipoDeEmprego tipoDeEmprego = criarTipoDeEmpregoAprovado(3L, "Desenvolvedor Java");
        Pais pais = criarPais(1L, "Brasil", "BR");
        OportunidadeDeEmprego oportunidade = criarOportunidade(
                401L,
                "Kotlin e mensageria",
                Modalidade.HIBRIDO,
                tipoDeEmprego,
                perfilRecrutador,
                null,
                pais
        );

        when(usuarioAutenticadoService.obterPerfilRecrutadorAutenticado()).thenReturn(perfilRecrutador);
        when(oportunidadeDeEmpregoRepository.findByPerfilRecrutadorId(15L))
                .thenReturn(List.of(oportunidade));

        List<?> resposta = oportunidadeDeEmpregoService.listarOportunidadesDoRecrutadorAutenticado();

        assertInstanceOf(OportunidadeDeEmpregoResponseDTO.class, resposta.get(0));
        assertNotSame(oportunidade, resposta.get(0));
    }

    @Test
    void deveRemoverOportunidadeDoRecrutadorAutenticadoComSucesso() {
        PerfilRecrutador perfilRecrutador =
                criarPerfilRecrutadorAutenticado(41L, "Rafaela Nunes", "Empresa Legada");
        TipoDeEmprego tipoDeEmprego = criarTipoDeEmpregoAprovado(7L, "Desenvolvedor Java");
        Pais pais = criarPais(1L, "Brasil", "BR");
        OportunidadeDeEmprego oportunidade = criarOportunidade(
                701L,
                "Backend distribuido",
                Modalidade.REMOTO,
                tipoDeEmprego,
                perfilRecrutador,
                null,
                pais
        );

        when(usuarioAutenticadoService.obterPerfilRecrutadorAutenticado()).thenReturn(perfilRecrutador);
        when(oportunidadeDeEmpregoRepository.findById(701L)).thenReturn(Optional.of(oportunidade));

        oportunidadeDeEmpregoService.removerOportunidadeDeEmprego(701L);

        verify(usuarioAutenticadoService).obterPerfilRecrutadorAutenticado();
        verify(oportunidadeDeEmpregoRepository).findById(701L);
        verify(oportunidadeDeEmpregoRepository).removerTodosInteressesDaVaga(701L);
        verify(oportunidadeDeEmpregoRepository).delete(oportunidade);
        verifyNoInteractions(
                tipoDeEmpregoRepository,
                paisRepository,
                estadoRepository,
                cidadeRepository,
                localidadeResolucaoService
        );
    }

    @Test
    void deveBloquearRemocaoDeOportunidadeDeOutroRecrutador() {
        PerfilRecrutador perfilRecrutadorAutenticado =
                criarPerfilRecrutadorAutenticado(41L, "Rafaela Nunes", "Empresa Legada");
        PerfilRecrutador outroRecrutador =
                criarPerfilRecrutadorAutenticado(42L, "Diego Ramos", "Outra Empresa");
        TipoDeEmprego tipoDeEmprego = criarTipoDeEmpregoAprovado(7L, "Desenvolvedor Java");
        Pais pais = criarPais(1L, "Brasil", "BR");
        OportunidadeDeEmprego oportunidade = criarOportunidade(
                702L,
                "Plataforma de pagamentos",
                Modalidade.HIBRIDO,
                tipoDeEmprego,
                outroRecrutador,
                null,
                pais
        );

        when(usuarioAutenticadoService.obterPerfilRecrutadorAutenticado()).thenReturn(perfilRecrutadorAutenticado);
        when(oportunidadeDeEmpregoRepository.findById(702L)).thenReturn(Optional.of(oportunidade));

        assertThrows(
                BusinessRuleException.class,
                () -> oportunidadeDeEmpregoService.removerOportunidadeDeEmprego(702L)
        );

        verify(usuarioAutenticadoService).obterPerfilRecrutadorAutenticado();
        verify(oportunidadeDeEmpregoRepository).findById(702L);
        verify(oportunidadeDeEmpregoRepository, never()).removerTodosInteressesDaVaga(702L);
        verify(oportunidadeDeEmpregoRepository, never()).delete(oportunidade);
        verifyNoInteractions(
                tipoDeEmpregoRepository,
                paisRepository,
                estadoRepository,
                cidadeRepository,
                localidadeResolucaoService
        );
    }

    private PerfilRecrutador criarPerfilRecrutadorAutenticado(Long id, String nomeUsuario, String empresaLegada) {
        Usuario usuario = new Usuario(
                "12345678909",
                nomeUsuario,
                "83999999999",
                "recrutador" + id + "@teste.com",
                "Senha@123"
        );
        ReflectionTestUtils.setField(usuario, "id", id);

        PerfilRecrutador perfilRecrutador = new PerfilRecrutador(null, empresaLegada);
        ReflectionTestUtils.setField(perfilRecrutador, "id", id);
        usuario.adicionarPerfilRecrutador(perfilRecrutador);

        return perfilRecrutador;
    }

    private OportunidadeDeEmprego criarOportunidade(
            Long id,
            String descricao,
            Modalidade modalidade,
            TipoDeEmprego tipoDeEmprego,
            PerfilRecrutador perfilRecrutador,
            Empresa empresa,
            Pais pais
    ) {
        OportunidadeDeEmprego oportunidade = new OportunidadeDeEmprego(
                descricao,
                tipoDeEmprego,
                modalidade,
                new Localidade(pais),
                perfilRecrutador,
                empresa
        );
        ReflectionTestUtils.setField(oportunidade, "id", id);
        return oportunidade;
    }

    private TipoDeEmprego criarTipoDeEmpregoAprovado(Long id, String titulo) {
        TipoDeEmprego tipoDeEmprego = TipoDeEmprego.criarTipoDeEmpregoAdmin(titulo, "Descrição");
        ReflectionTestUtils.setField(tipoDeEmprego, "id", id);
        return tipoDeEmprego;
    }

    private Pais criarPais(Long id, String nome, String sigla) {
        Pais pais = new Pais(nome, sigla);
        ReflectionTestUtils.setField(pais, "id", id);
        return pais;
    }

    private Empresa criarEmpresa(Long id, String nome) {
        Empresa empresa = new Empresa(nome, null, null, null, null, null);
        ReflectionTestUtils.setField(empresa, "id", id);
        return empresa;
    }
}
