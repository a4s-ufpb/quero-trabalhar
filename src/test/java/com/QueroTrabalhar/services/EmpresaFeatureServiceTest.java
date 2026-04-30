package com.QueroTrabalhar.services;

import com.QueroTrabalhar.domain.dtos.empresa.EmpresaRequestDTO;
import com.QueroTrabalhar.domain.dtos.empresa.EmpresaResponseDTO;
import com.QueroTrabalhar.domain.dtos.oportunidadeDeEmprego.OportunidadeDeEmpregoResponseDTO;
import com.QueroTrabalhar.domain.dtos.perfilRecrutador.RecrutadorDaEmpresaResponseDTO;
import com.QueroTrabalhar.domain.entity.Empresa;
import com.QueroTrabalhar.domain.entity.OportunidadeDeEmprego;
import com.QueroTrabalhar.domain.entity.PerfilRecrutador;
import com.QueroTrabalhar.domain.entity.TipoDeEmprego;
import com.QueroTrabalhar.domain.entity.Usuario;
import com.QueroTrabalhar.domain.entity.localidade.Cidade;
import com.QueroTrabalhar.domain.entity.localidade.Estado;
import com.QueroTrabalhar.domain.entity.localidade.Localidade;
import com.QueroTrabalhar.domain.entity.localidade.Pais;
import com.QueroTrabalhar.domain.enums.Modalidade;
import com.QueroTrabalhar.domain.enums.StatusVinculoEmpresa;
import com.QueroTrabalhar.repository.CidadeRepository;
import com.QueroTrabalhar.repository.EmpresaRepository;
import com.QueroTrabalhar.repository.EstadoRepository;
import com.QueroTrabalhar.repository.OportunidadeDeEmpregoRepository;
import com.QueroTrabalhar.repository.PaisRepository;
import com.QueroTrabalhar.repository.PerfilRecrutadorRepository;
import com.QueroTrabalhar.services.exceptions.BusinessRuleException;
import com.QueroTrabalhar.services.exceptions.ObjectNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmpresaFeatureServiceTest {

    @Mock
    private EmpresaRepository empresaRepository;

    @Mock
    private PerfilRecrutadorRepository perfilRecrutadorRepository;

    @Mock
    private OportunidadeDeEmpregoRepository oportunidadeDeEmpregoRepository;

    @Mock
    private PaisRepository paisRepository;

    @Mock
    private EstadoRepository estadoRepository;

    @Mock
    private CidadeRepository cidadeRepository;

    @InjectMocks
    private EmpresaService empresaService;

    @Test
    void deveCriarEmpresaComLocalidadeValida() {
        Pais pais = criarPais(1L, "Brasil", "BR");
        Estado estado = criarEstado(10L, "Paraiba", "PB", pais);
        Cidade cidade = criarCidade(100L, "Joao Pessoa", estado);
        EmpresaRequestDTO dto = new EmpresaRequestDTO(
                "  Empresa ACME  ",
                "  Plataforma de empregabilidade  ",
                "  https://acme.com  ",
                "  contato@acme.com  ",
                "  83999999999  ",
                pais.getId(),
                estado.getId(),
                cidade.getId()
        );

        when(paisRepository.findById(pais.getId())).thenReturn(Optional.of(pais));
        when(estadoRepository.findById(estado.getId())).thenReturn(Optional.of(estado));
        when(cidadeRepository.findById(cidade.getId())).thenReturn(Optional.of(cidade));
        when(empresaRepository.save(any(Empresa.class))).thenAnswer(invocation -> {
            Empresa empresaSalva = invocation.getArgument(0);
            ReflectionTestUtils.setField(empresaSalva, "id", 50L);
            return empresaSalva;
        });

        EmpresaResponseDTO resposta = empresaService.criarEmpresa(dto);

        ArgumentCaptor<Empresa> empresaCaptor = ArgumentCaptor.forClass(Empresa.class);
        verify(empresaRepository).save(empresaCaptor.capture());
        Empresa empresaSalva = empresaCaptor.getValue();

        assertEquals(50L, resposta.id());
        assertEquals("Empresa ACME", resposta.nome());
        assertEquals("Plataforma de empregabilidade", resposta.descricao());
        assertEquals("https://acme.com", resposta.site());
        assertEquals("contato@acme.com", resposta.emailPublico());
        assertEquals("83999999999", resposta.telefonePublico());
        assertEquals(1L, resposta.paisId());
        assertEquals(10L, resposta.estadoId());
        assertEquals(100L, resposta.cidadeId());
        assertSame(pais, empresaSalva.getLocalidade().getPais());
        assertSame(estado, empresaSalva.getLocalidade().getEstado());
        assertSame(cidade, empresaSalva.getLocalidade().getCidade());
    }

    @Test
    void deveLancarObjectNotFoundExceptionQuandoPaisNaoExistir() {
        EmpresaRequestDTO dto = new EmpresaRequestDTO(
                "Empresa ACME",
                null,
                null,
                null,
                null,
                99L,
                null,
                null
        );

        when(paisRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> empresaService.criarEmpresa(dto));

        verify(empresaRepository, never()).save(any(Empresa.class));
    }

    @Test
    void deveLancarBusinessRuleExceptionQuandoCidadeForInformadaSemEstado() {
        EmpresaRequestDTO dto = new EmpresaRequestDTO(
                "Empresa ACME",
                null,
                null,
                null,
                null,
                1L,
                null,
                100L
        );

        assertThrows(BusinessRuleException.class, () -> empresaService.criarEmpresa(dto));

        verify(paisRepository, never()).findById(any(Long.class));
        verify(empresaRepository, never()).save(any(Empresa.class));
    }

    @Test
    void deveLancarBusinessRuleExceptionQuandoEstadoNaoPertencerAoPais() {
        Pais brasil = criarPais(1L, "Brasil", "BR");
        Pais argentina = criarPais(2L, "Argentina", "AR");
        Estado cordoba = criarEstado(10L, "Cordoba", "CB", argentina);
        EmpresaRequestDTO dto = new EmpresaRequestDTO(
                "Empresa ACME",
                null,
                null,
                null,
                null,
                brasil.getId(),
                cordoba.getId(),
                null
        );

        when(paisRepository.findById(brasil.getId())).thenReturn(Optional.of(brasil));
        when(estadoRepository.findById(cordoba.getId())).thenReturn(Optional.of(cordoba));

        assertThrows(BusinessRuleException.class, () -> empresaService.criarEmpresa(dto));

        verify(cidadeRepository, never()).findById(any(Long.class));
        verify(empresaRepository, never()).save(any(Empresa.class));
    }

    @Test
    void deveLancarBusinessRuleExceptionQuandoCidadeNaoPertencerAoEstado() {
        Pais pais = criarPais(1L, "Brasil", "BR");
        Estado paraiba = criarEstado(10L, "Paraiba", "PB", pais);
        Estado pernambuco = criarEstado(20L, "Pernambuco", "PE", pais);
        Cidade recife = criarCidade(100L, "Recife", pernambuco);
        EmpresaRequestDTO dto = new EmpresaRequestDTO(
                "Empresa ACME",
                null,
                null,
                null,
                null,
                pais.getId(),
                paraiba.getId(),
                recife.getId()
        );

        when(paisRepository.findById(pais.getId())).thenReturn(Optional.of(pais));
        when(estadoRepository.findById(paraiba.getId())).thenReturn(Optional.of(paraiba));
        when(cidadeRepository.findById(recife.getId())).thenReturn(Optional.of(recife));

        assertThrows(BusinessRuleException.class, () -> empresaService.criarEmpresa(dto));

        verify(empresaRepository, never()).save(any(Empresa.class));
    }

    @Test
    void deveListarRecrutadoresAprovadosDaEmpresa() {
        Empresa empresa = criarEmpresa(1L, "Empresa ACME");
        PerfilRecrutador recrutadorComNomeZeca = criarPerfilRecrutador(10L, "Zeca", empresa, StatusVinculoEmpresa.APROVADO);
        PerfilRecrutador recrutadorComNomeAna = criarPerfilRecrutador(11L, "Ana", empresa, StatusVinculoEmpresa.APROVADO);

        when(empresaRepository.findById(empresa.getId())).thenReturn(Optional.of(empresa));
        when(perfilRecrutadorRepository.findByEmpresaVinculadaIdAndStatusVinculoEmpresa(
                empresa.getId(),
                StatusVinculoEmpresa.APROVADO
        )).thenReturn(List.of(recrutadorComNomeZeca, recrutadorComNomeAna));

        List<RecrutadorDaEmpresaResponseDTO> resposta =
                empresaService.listarRecrutadoresAprovadosDaEmpresa(empresa.getId());

        assertEquals(2, resposta.size());
        assertEquals("Ana", resposta.get(0).nome());
        assertEquals("Zeca", resposta.get(1).nome());
        assertEquals(empresa.getId(), resposta.get(0).empresaId());
        verify(perfilRecrutadorRepository)
                .findByEmpresaVinculadaIdAndStatusVinculoEmpresa(empresa.getId(), StatusVinculoEmpresa.APROVADO);
    }

    @Test
    void deveListarOportunidadesPublicadasEmNomeDaEmpresa() {
        Empresa empresa = criarEmpresa(1L, "Empresa ACME");
        Pais pais = criarPais(1L, "Brasil", "BR");
        TipoDeEmprego tipoDeEmprego = criarTipoDeEmpregoAprovado(20L, "Desenvolvedor Backend");
        PerfilRecrutador recrutador = criarPerfilRecrutador(10L, "Ana", empresa, StatusVinculoEmpresa.APROVADO);
        OportunidadeDeEmprego oportunidade = new OportunidadeDeEmprego(
                "Vaga Java",
                tipoDeEmprego,
                Modalidade.REMOTO,
                new Localidade(pais),
                recrutador,
                empresa
        );
        ReflectionTestUtils.setField(oportunidade, "id", 100L);

        when(empresaRepository.findById(empresa.getId())).thenReturn(Optional.of(empresa));
        when(oportunidadeDeEmpregoRepository.findByEmpresaId(empresa.getId())).thenReturn(List.of(oportunidade));

        List<OportunidadeDeEmpregoResponseDTO> resposta =
                empresaService.listarOportunidadesDaEmpresa(empresa.getId());

        assertEquals(1, resposta.size());
        assertEquals(100L, resposta.get(0).id());
        assertEquals("Vaga Java", resposta.get(0).descricao());
        assertEquals(empresa.getId(), resposta.get(0).empresaId());
        assertEquals("Empresa ACME", resposta.get(0).empresaNome());
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

    private Empresa criarEmpresa(Long id, String nome) {
        Empresa empresa = new Empresa(nome, null, null, null, null, null);
        ReflectionTestUtils.setField(empresa, "id", id);
        return empresa;
    }

    private PerfilRecrutador criarPerfilRecrutador(
            Long id,
            String nomeUsuario,
            Empresa empresa,
            StatusVinculoEmpresa statusVinculoEmpresa
    ) {
        Usuario usuario = new Usuario(
                "12345678909",
                nomeUsuario,
                "83999999999",
                nomeUsuario.toLowerCase() + "@email.com",
                "SenhaForte123"
        );
        ReflectionTestUtils.setField(usuario, "id", id);

        PerfilRecrutador perfilRecrutador = new PerfilRecrutador(usuario, "Empresa antiga");
        ReflectionTestUtils.setField(perfilRecrutador, "id", id);
        usuario.adicionarPerfilRecrutador(perfilRecrutador);
        perfilRecrutador.setEmpresaVinculada(empresa);
        perfilRecrutador.setStatusVinculoEmpresa(statusVinculoEmpresa);
        return perfilRecrutador;
    }

    private TipoDeEmprego criarTipoDeEmpregoAprovado(Long id, String titulo) {
        TipoDeEmprego tipoDeEmprego = TipoDeEmprego.criarTipoDeEmpregoAdmin(titulo, "Descricao");
        ReflectionTestUtils.setField(tipoDeEmprego, "id", id);
        return tipoDeEmprego;
    }
}
