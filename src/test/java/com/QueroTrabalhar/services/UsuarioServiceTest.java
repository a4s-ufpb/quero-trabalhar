package com.QueroTrabalhar.services;

import com.QueroTrabalhar.domain.dtos.usuario.UsuarioRequestDTO;
import com.QueroTrabalhar.domain.dtos.usuario.UsuarioResponseDTO;
import com.QueroTrabalhar.domain.entity.Usuario;
import com.QueroTrabalhar.repository.OportunidadeDeEmpregoRepository;
import com.QueroTrabalhar.repository.PerfilCandidatoRepository;
import com.QueroTrabalhar.repository.PerfilRecrutadorRepository;
import com.QueroTrabalhar.repository.UsuarioRepository;
import com.QueroTrabalhar.services.exceptions.DataIntegrityViolationException;
import com.QueroTrabalhar.services.exceptions.DuplicateResourceException;
import com.QueroTrabalhar.services.exceptions.ObjectNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PerfilCandidatoRepository perfilCandidatoRepository;

    @Mock
    private PerfilRecrutadorRepository perfilRecrutadorRepository;

    @Mock
    private OportunidadeDeEmpregoRepository oportunidadeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void deveCadastrarUsuarioComSucesso() {
        // Arrange
        UsuarioRequestDTO request = new UsuarioRequestDTO(
                "12345678909",
                "Fernanda Costa",
                "83999998888",
                "fernanda.costa@teste.com",
                "Senha@123"
        );
        String senhaCodificada = "senha-codificada";

        when(usuarioRepository.existsByCpf(request.cpf())).thenReturn(false);
        when(usuarioRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.senha())).thenReturn(senhaCodificada);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuarioSalvo = invocation.getArgument(0);
            ReflectionTestUtils.setField(usuarioSalvo, "id", 10L);
            return usuarioSalvo;
        });

        // Act
        UsuarioResponseDTO resposta = usuarioService.cadastrarUsuario(request);

        // Assert
        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);

        assertAll(
                () -> assertNotNull(resposta),
                () -> assertEquals(10L, resposta.id()),
                () -> assertEquals(request.nome(), resposta.nome()),
                () -> assertEquals(request.telefone(), resposta.telefone()),
                () -> assertEquals(request.email(), resposta.email())
        );

        verify(passwordEncoder).encode(request.senha());
        verify(usuarioRepository).save(usuarioCaptor.capture());

        Usuario usuarioSalvo = usuarioCaptor.getValue();
        assertAll(
                () -> assertEquals(request.cpf(), usuarioSalvo.getCpf()),
                () -> assertEquals(request.nome(), usuarioSalvo.getNome()),
                () -> assertEquals(request.telefone(), usuarioSalvo.getTelefone()),
                () -> assertEquals(request.email(), usuarioSalvo.getEmail()),
                () -> assertEquals(senhaCodificada, usuarioSalvo.getSenha()),
                () -> assertTrue(usuarioSalvo.ehCandidato()),
                () -> assertNotNull(usuarioSalvo.getPerfilCandidato())
        );
        verifyNoInteractions(usuarioAutenticadoService, perfilCandidatoRepository, perfilRecrutadorRepository, oportunidadeRepository);
    }

    @Test
    void deveBloquearCadastroQuandoCpfJaExistir() {
        // Arrange
        UsuarioRequestDTO request = new UsuarioRequestDTO(
                "12345678909",
                "Fernanda Costa",
                "83999998888",
                "fernanda.costa@teste.com",
                "Senha@123"
        );
        when(usuarioRepository.existsByCpf(request.cpf())).thenReturn(true);

        // Act
        assertThrows(DataIntegrityViolationException.class, () -> usuarioService.cadastrarUsuario(request));

        // Assert
        verify(usuarioRepository).existsByCpf(request.cpf());
        verify(usuarioRepository, never()).findByEmail(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(usuarioRepository, never()).save(any(Usuario.class));
        verifyNoInteractions(usuarioAutenticadoService, perfilCandidatoRepository, perfilRecrutadorRepository, oportunidadeRepository);
    }

    @Test
    void deveBloquearCadastroQuandoEmailJaExistir() {
        // Arrange
        UsuarioRequestDTO request = new UsuarioRequestDTO(
                "12345678909",
                "Fernanda Costa",
                "83999998888",
                "fernanda.costa@teste.com",
                "Senha@123"
        );
        Usuario usuarioExistente = criarUsuario(20L, "Usuario Existente", request.email());

        when(usuarioRepository.existsByCpf(request.cpf())).thenReturn(false);
        when(usuarioRepository.findByEmail(request.email())).thenReturn(Optional.of(usuarioExistente));

        // Act
        assertThrows(DuplicateResourceException.class, () -> usuarioService.cadastrarUsuario(request));

        // Assert
        verify(usuarioRepository).existsByCpf(request.cpf());
        verify(usuarioRepository).findByEmail(request.email());
        verify(passwordEncoder, never()).encode(anyString());
        verify(usuarioRepository, never()).save(any(Usuario.class));
        verifyNoInteractions(usuarioAutenticadoService, perfilCandidatoRepository, perfilRecrutadorRepository, oportunidadeRepository);
    }

    @Test
    void deveDeletarUsuarioPorIdComSucesso() {
        // Arrange
        Long usuarioId = 30L;
        Usuario usuario = criarUsuario(usuarioId, "Marina Souza", "marina.souza@teste.com");
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));

        // Act
        usuarioService.deletarUsuarioPorId(usuarioId);

        // Assert
        verify(usuarioRepository).findById(usuarioId);
        verify(usuarioRepository).delete(same(usuario));
        verifyNoInteractions(usuarioAutenticadoService, passwordEncoder, perfilCandidatoRepository, perfilRecrutadorRepository, oportunidadeRepository);
    }

    @Test
    void deveLancarObjectNotFoundExceptionAoDeletarUsuarioPorIdInexistente() {
        // Arrange
        Long usuarioId = 99L;
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());

        // Act
        assertThrows(ObjectNotFoundException.class, () -> usuarioService.deletarUsuarioPorId(usuarioId));

        // Assert
        verify(usuarioRepository).findById(usuarioId);
        verify(usuarioRepository, never()).delete(any(Usuario.class));
        verifyNoInteractions(usuarioAutenticadoService, passwordEncoder, perfilCandidatoRepository, perfilRecrutadorRepository, oportunidadeRepository);
    }

    @Test
    void deveRemoverUsuarioAutenticadoComSucesso() {
        // Arrange
        Usuario usuarioAutenticado = criarUsuario(40L, "Carlos Lima", "carlos.lima@teste.com");
        when(usuarioAutenticadoService.obterUsuarioAutenticado()).thenReturn(usuarioAutenticado);

        // Act
        usuarioService.meRemover();

        // Assert
        verify(usuarioAutenticadoService).obterUsuarioAutenticado();
        verify(usuarioRepository).delete(same(usuarioAutenticado));
        verifyNoInteractions(passwordEncoder, perfilCandidatoRepository, perfilRecrutadorRepository, oportunidadeRepository);
    }

    private Usuario criarUsuario(Long id, String nome, String email) {
        Usuario usuario = new Usuario("12345678909", nome, "83999990000", email, "senha-codificada");
        ReflectionTestUtils.setField(usuario, "id", id);
        return usuario;
    }
}
