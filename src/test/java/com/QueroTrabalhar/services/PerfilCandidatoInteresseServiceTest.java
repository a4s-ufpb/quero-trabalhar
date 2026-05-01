package com.QueroTrabalhar.services;

import com.QueroTrabalhar.domain.dtos.oportunidadeDeEmprego.OportunidadeDeEmpregoResponseDTO;
import com.QueroTrabalhar.domain.entity.Empresa;
import com.QueroTrabalhar.domain.entity.OportunidadeDeEmprego;
import com.QueroTrabalhar.domain.entity.PerfilCandidato;
import com.QueroTrabalhar.domain.entity.PerfilRecrutador;
import com.QueroTrabalhar.domain.entity.TipoDeEmprego;
import com.QueroTrabalhar.domain.entity.Usuario;
import com.QueroTrabalhar.domain.entity.localidade.Localidade;
import com.QueroTrabalhar.domain.entity.localidade.Pais;
import com.QueroTrabalhar.domain.enums.Modalidade;
import com.QueroTrabalhar.repository.OportunidadeDeEmpregoRepository;
import com.QueroTrabalhar.repository.PerfilCandidatoRepository;
import com.QueroTrabalhar.services.exceptions.BusinessRuleException;
import com.QueroTrabalhar.services.exceptions.ObjectNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerfilCandidatoInteresseServiceTest {

    @Mock
    private PerfilCandidatoRepository perfilCandidatoRepository;

    @Mock
    private OportunidadeDeEmpregoRepository oportunidadeDeEmpregoRepository;

    @Mock
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @InjectMocks
    private PerfilCandidatoService perfilCandidatoService;

    @Test
    void deveDemonstrarInteresseEmVagaComCandidatoAutenticado() {
        // Arrange
        Long vagaId = 100L;
        PerfilCandidato perfilCandidato = criarPerfilCandidatoAutenticado(1L, "João da Silva");
        OportunidadeDeEmprego vaga = criarOportunidade(
                vagaId,
                "Pessoa desenvolvedora Java 21",
                Modalidade.REMOTO,
                criarTipoDeEmpregoAprovado(5L, "Backend"),
                criarPerfilRecrutador(20L, "Marina Costa"),
                null,
                criarPais(1L, "Brasil", "BR")
        );

        when(usuarioAutenticadoService.obterPerfilCandidatoAutenticado()).thenReturn(perfilCandidato);
        when(oportunidadeDeEmpregoRepository.findById(vagaId)).thenReturn(Optional.of(vaga));

        // Act
        perfilCandidatoService.demonstrarInteresseEmVaga(vagaId);

        // Assert
        assertTrue(perfilCandidato.getVagasDeInteresse().contains(vaga));
        verify(perfilCandidatoRepository).save(perfilCandidato);
    }

    @Test
    void deveBloquearInteresseDuplicadoNaMesmaVaga() {
        // Arrange
        Long vagaId = 101L;
        PerfilCandidato perfilCandidato = criarPerfilCandidatoAutenticado(2L, "Lívia Andrade");
        OportunidadeDeEmprego vaga = criarOportunidade(
                vagaId,
                "Tech lead para microsserviços",
                Modalidade.HIBRIDO,
                criarTipoDeEmpregoAprovado(6L, "Liderança técnica"),
                criarPerfilRecrutador(21L, "Rafael Dias"),
                null,
                criarPais(1L, "Brasil", "BR")
        );
        perfilCandidato.demonstrarInteresse(vaga);

        when(usuarioAutenticadoService.obterPerfilCandidatoAutenticado()).thenReturn(perfilCandidato);
        when(oportunidadeDeEmpregoRepository.findById(vagaId)).thenReturn(Optional.of(vaga));

        // Act / Assert
        assertThrows(
                BusinessRuleException.class,
                () -> perfilCandidatoService.demonstrarInteresseEmVaga(vagaId)
        );

        verify(perfilCandidatoRepository, never()).save(any(PerfilCandidato.class));
    }

    @Test
    void deveLancarObjectNotFoundExceptionQuandoVagaNaoExistirAoDemonstrarInteresse() {
        // Arrange
        Long vagaId = 999L;
        PerfilCandidato perfilCandidato = criarPerfilCandidatoAutenticado(3L, "Érica Pires");

        when(usuarioAutenticadoService.obterPerfilCandidatoAutenticado()).thenReturn(perfilCandidato);
        when(oportunidadeDeEmpregoRepository.findById(vagaId)).thenReturn(Optional.empty());

        // Act / Assert
        assertThrows(
                ObjectNotFoundException.class,
                () -> perfilCandidatoService.demonstrarInteresseEmVaga(vagaId)
        );

        verify(perfilCandidatoRepository, never()).save(any(PerfilCandidato.class));
    }

    @Test
    void deveRemoverInteresseEmVagaComCandidatoAutenticado() {
        // Arrange
        Long vagaId = 102L;
        PerfilCandidato perfilCandidato = criarPerfilCandidatoAutenticado(4L, "Carlos Henrique");
        OportunidadeDeEmprego vaga = criarOportunidade(
                vagaId,
                "Especialista em integrações REST",
                Modalidade.PRESENCIAL,
                criarTipoDeEmpregoAprovado(7L, "Integrações"),
                criarPerfilRecrutador(22L, "Fernanda Rocha"),
                null,
                criarPais(1L, "Brasil", "BR")
        );
        perfilCandidato.demonstrarInteresse(vaga);

        when(usuarioAutenticadoService.obterPerfilCandidatoAutenticado()).thenReturn(perfilCandidato);
        when(oportunidadeDeEmpregoRepository.findById(vagaId)).thenReturn(Optional.of(vaga));

        // Act
        perfilCandidatoService.removerInteresseEmVaga(vagaId);

        // Assert
        assertFalse(perfilCandidato.getVagasDeInteresse().contains(vaga));
        verify(perfilCandidatoRepository).save(perfilCandidato);
    }

    @Test
    void deveBloquearRemocaoDeInteresseInexistente() {
        // Arrange
        Long vagaId = 103L;
        PerfilCandidato perfilCandidato = criarPerfilCandidatoAutenticado(5L, "Ana Paula");
        OportunidadeDeEmprego vaga = criarOportunidade(
                vagaId,
                "Desenvolvimento orientado a eventos",
                Modalidade.REMOTO,
                criarTipoDeEmpregoAprovado(8L, "Mensageria"),
                criarPerfilRecrutador(23L, "Patrícia Mendes"),
                null,
                criarPais(1L, "Brasil", "BR")
        );

        when(usuarioAutenticadoService.obterPerfilCandidatoAutenticado()).thenReturn(perfilCandidato);
        when(oportunidadeDeEmpregoRepository.findById(vagaId)).thenReturn(Optional.of(vaga));

        // Act / Assert
        assertThrows(
                BusinessRuleException.class,
                () -> perfilCandidatoService.removerInteresseEmVaga(vagaId)
        );

        verify(perfilCandidatoRepository, never()).save(any(PerfilCandidato.class));
    }

    @Test
    void deveLancarObjectNotFoundExceptionQuandoVagaNaoExistirAoRemoverInteresse() {
        // Arrange
        Long vagaId = 1000L;
        PerfilCandidato perfilCandidato = criarPerfilCandidatoAutenticado(6L, "Marcelo Júnior");

        when(usuarioAutenticadoService.obterPerfilCandidatoAutenticado()).thenReturn(perfilCandidato);
        when(oportunidadeDeEmpregoRepository.findById(vagaId)).thenReturn(Optional.empty());

        // Act / Assert
        assertThrows(
                ObjectNotFoundException.class,
                () -> perfilCandidatoService.removerInteresseEmVaga(vagaId)
        );

        verify(perfilCandidatoRepository, never()).save(any(PerfilCandidato.class));
    }

    @Test
    void deveListarMinhasVagasDeInteresse() {
        // Arrange
        PerfilCandidato perfilCandidato = criarPerfilCandidatoAutenticado(7L, "Joana D'Ávila");
        PerfilRecrutador recrutador = criarPerfilRecrutador(30L, "Camila Araújo");
        Empresa empresa = criarEmpresa(40L, "Inovação Pública");
        OportunidadeDeEmprego vaga = criarOportunidade(
                104L,
                "Backend Java para serviços digitais",
                Modalidade.HIBRIDO,
                criarTipoDeEmpregoAprovado(9L, "Backend Sênior"),
                recrutador,
                empresa,
                criarPais(1L, "Brasil", "BR")
        );
        perfilCandidato.demonstrarInteresse(vaga);

        when(usuarioAutenticadoService.obterPerfilCandidatoAutenticado()).thenReturn(perfilCandidato);

        // Act
        List<?> resposta = perfilCandidatoService.listarMinhasVagasDeInteresse();

        // Assert
        assertEquals(1, resposta.size());
        assertInstanceOf(OportunidadeDeEmpregoResponseDTO.class, resposta.get(0));
        assertNotSame(vaga, resposta.get(0));

        OportunidadeDeEmpregoResponseDTO dto = (OportunidadeDeEmpregoResponseDTO) resposta.get(0);
        assertAll(
                () -> assertEquals(104L, dto.id()),
                () -> assertEquals("Backend Java para serviços digitais", dto.descricao()),
                () -> assertEquals(30L, dto.recrutadorId()),
                () -> assertEquals("Camila Araújo", dto.recrutadorNome()),
                () -> assertEquals(40L, dto.empresaId()),
                () -> assertEquals("Inovação Pública", dto.empresaNome())
        );
        verify(usuarioAutenticadoService).obterPerfilCandidatoAutenticado();
        verifyNoInteractions(oportunidadeDeEmpregoRepository, perfilCandidatoRepository);
    }

    private PerfilCandidato criarPerfilCandidatoAutenticado(Long id, String nomeUsuario) {
        Usuario usuario = new Usuario(
                "12345678909",
                nomeUsuario,
                "83999999999",
                "candidato" + id + "@teste.com",
                "Senha@123"
        );
        ReflectionTestUtils.setField(usuario, "id", id);

        PerfilCandidato perfilCandidato = new PerfilCandidato(usuario);
        ReflectionTestUtils.setField(perfilCandidato, "id", id);
        usuario.adicionarPerfilCandidato(perfilCandidato);

        return perfilCandidato;
    }

    private PerfilRecrutador criarPerfilRecrutador(Long id, String nomeUsuario) {
        Usuario usuario = new Usuario(
                "98765432100",
                nomeUsuario,
                "83988888888",
                "recrutador" + id + "@teste.com",
                "Senha@123"
        );
        ReflectionTestUtils.setField(usuario, "id", id);

        PerfilRecrutador perfilRecrutador = new PerfilRecrutador(null, "Empresa Legada");
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
        TipoDeEmprego tipoDeEmprego = TipoDeEmprego.criarTipoDeEmpregoAdmin(titulo, "Descrição do tipo");
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
