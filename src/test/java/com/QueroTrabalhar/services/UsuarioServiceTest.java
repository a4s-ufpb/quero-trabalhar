package com.QueroTrabalhar.services;

import com.QueroTrabalhar.domain.dtos.usuario.UsuarioResponseDTO;
import com.QueroTrabalhar.domain.dtos.usuario.UsuarioResponseDTO;
import com.QueroTrabalhar.domain.entity.Usuario;
import com.QueroTrabalhar.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    // Atributos base
    private Long defaultId;

    // Objetos para teste
    private Usuario user;
    private Usuario user1;
    private Usuario user2;

    @BeforeEach
    void setUp() throws Exception {
        defaultId = 1L;

        // 1. Usando o novo construtor da entidade (Cpf, Nome, Telefone, Email, Senha)
        user = new Usuario("12345678964", "Fernanda", "83912345678", "fernanda23@email.com", "senhaForte123");
        injetarId(user, defaultId);

        user1 = new Usuario("12345678909", "Fernanda", "83912345675", "fernanda@email.com", "senhaForte123");
        injetarId(user1, 2L);

        user2 = new Usuario("12345678911", "Vitória", "83912345674", "vitoria@email.com", "senhaForte123");
        injetarId(user2, 3L);

        // As listas de experiências e interesses agora são responsabilidade do PerfilCandidato, 
        // então não precisamos mais mockar isso no teste básico de Usuario.
    }

    /**
     * Helper para injetar um ID na entidade sem precisar criarNoCatalogo um método setId() público
     * que violaria as regras de encapsulamento e segurança.
     */
    private void injetarId(Usuario usuario, Long id) throws Exception {
        Field field = Usuario.class.getDeclaredField("id");
        field.setAccessible(true);
        field.set(usuario, id);
    }

    @Test
    public void deveBuscarPorIdComSucesso() {
        // Arrange (Preparação)
        Mockito.when(usuarioRepository.findById(defaultId)).thenReturn(Optional.of(user));

        // Act (Ação)
        UsuarioResponseDTO resultado = usuarioService.buscarUsuarioPorId(defaultId);

        // Assert (Verificação)
        Assertions.assertNotNull(resultado);
        // Atenção: Use .getId(), ou .id() se o seu DTO for um Record Java
        Assertions.assertEquals(defaultId, resultado.id());
        Assertions.assertEquals("Fernanda", resultado.nome());

        Mockito.verify(usuarioRepository, Mockito.times(1)).findById(defaultId);
    }

    @Test
    public void deveLancarEntityNotFoundExceptionQuandoUsuarioNaoEncontrado() {
        Mockito.when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            usuarioService.buscarUsuarioPorId(99L);
        });

        Mockito.verify(usuarioRepository).findById(99L);
    }

    @Test
    public void deveRetornarListaDeUsuariosComSucesso() {
        List<Usuario> users = Arrays.asList(user1, user2);
        Mockito.when(usuarioRepository.findAll()).thenReturn(users);

        List<UsuarioResponseDTO> result = usuarioService.listarTodosUsuarios();

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(2, result.size());
    }

    @Test
    public void deveDeletarUsuario() {
        Mockito.when(usuarioRepository.existsById(defaultId)).thenReturn(true);
        Mockito.doNothing().when(usuarioRepository).deleteById(defaultId);

        usuarioService.deletarUsuarioPorId(defaultId);

        Mockito.verify(usuarioRepository).deleteById(defaultId);
    }

    @Test
    public void deveLancarExcecaoQuandoDeletarUsuarioNaoEncontrado() {
        Mockito.when(usuarioRepository.existsById(defaultId)).thenReturn(false);


        // O Service novo foi atualizado para lançar EntityNotFoundException
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            usuarioService.deletarUsuarioPorId(defaultId);
        });

        Mockito.verify(usuarioRepository).existsById(defaultId);
        Mockito.verifyNoMoreInteractions(usuarioRepository);
    }
}