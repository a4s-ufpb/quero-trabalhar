
package com.QueroTrabalhar.services;

import com.QueroTrabalhar.dtos.user.UserDTOResponse;
import com.QueroTrabalhar.entity.ExperienciaProfissional;
import com.QueroTrabalhar.entity.InteresseEmOportunidades;
import com.QueroTrabalhar.entity.Usuario;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    // Atributos dos objetos
    private Long defaultId;
    private String defaultName;
    private Long defaultCpf;
    private String defaultPhone;
    private String defaultEmail;
    private List<ExperienciaProfissional> defaultExperiencias;
    private InteresseEmOportunidades defaultInteresse;

    // Objetos para teste
    private Usuario user;
    private UserDTOResponse userDTOResponse;

    // objetos para o teste de lista
    private Usuario user1;
    private Usuario user2;


    @BeforeEach
    void setUp() {
        // --- Configurações para salvar/buscar por ID ---
        defaultId = 1L;
        defaultName = "Fernanda";
        defaultCpf = 12345678964L;
        defaultPhone = "83912345678";
        defaultEmail = "fernanda23@email.com";
        defaultExperiencias = new ArrayList<>();
        defaultInteresse = new InteresseEmOportunidades();

        // Configura um User
        user = new Usuario(
                defaultId,
                defaultCpf,
                defaultName,
                defaultPhone,
                defaultEmail);

        // Configura um UserDTO

        userDTOResponse = new UserDTOResponse();
        userDTOResponse.setCpf(defaultCpf);
        userDTOResponse.setNome(defaultName);
        userDTOResponse.setTelefone(defaultPhone);
        userDTOResponse.setEmail(defaultEmail);
        userDTOResponse.setExperienciaProfissionals(defaultExperiencias);
        userDTOResponse.setInteresseEmEmpregos(defaultInteresse);

        // --- Configurações adicionais para o teste de lista de usuários ---

        // Usuario 1
        user1 = new Usuario();
        user1.setId(1L);
        user1.setNome("Fernanda");
        user1.setCpf(12345678909L);
        user1.setEmail("fernanda@email.com");
        user1.setTelefone("1234567895");

        // Usuario 2
        user2 = new Usuario();
        user2.setId(2L);
        user2.setNome("Vitória");
        user2.setCpf(12345678911L);
        user2.setEmail("vitoria@email.com");
        user2.setTelefone("1234567894");
    }



//    @Test
//    public void deveSalvarUsuarioComSucesso() {
//
//        Mockito.when(usuarioRepository.save(Mockito.any(Usuario.class))).thenReturn(user);
//        UserDTOResponse userSaved = usuarioService.salvarUsuario(userDTOResponse);
//        Assertions.assertNotNull(userSaved);
//        Assertions.assertNotNull(userSaved.getId());
//        Assertions.assertEquals("Fernanda", userSaved.getNome());
//    }

    @Test
    public void deveBuscarPorIdComSucesso() {

        Mockito.when(usuarioRepository.findById(1L)).thenReturn(Optional.of(user));
        UserDTOResponse resultado = usuarioService.buscarUsuarioPorId(1L);
        Assertions.assertNotNull(resultado);
        Assertions.assertNotNull(resultado.getId());
        Assertions.assertEquals(1L, resultado.getId());
        Assertions.assertEquals(defaultName, resultado.getNome());
        Assertions.assertEquals(defaultCpf, resultado.getCpf());
        Assertions.assertEquals(defaultPhone, resultado.getTelefone());
        Assertions.assertEquals(defaultEmail, resultado.getEmail());
        Mockito.verify(usuarioRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    public void deveLancarEntityNotFoundExeptionQuandoUsuarioNaoEncontrado() {

        Mockito.when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
                    usuarioService.buscarUsuarioPorId(1L);});
        Mockito.verify(usuarioRepository).findById(1L);
    }

    @Test
    public void deveRetornarListaDeUsuariosComSucesso() {

        List<Usuario> users = Arrays.asList(user1, user2);
        Mockito.when(usuarioRepository.findAll()).thenReturn(users);
        List<UserDTOResponse> result = usuarioService.listarTodosUsuarios();
        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(2, result.size());

    }

    @Test
    public void deveDeletarUsuario() {
        Mockito.when(usuarioRepository.existsById(defaultId)).thenReturn(true);
        Mockito.doNothing().when(usuarioRepository).deleteById(defaultId);
        usuarioService.deletarUsuario(defaultId);
        Mockito.verify(usuarioRepository).deleteById(defaultId); // Verifica se deleteById foi chamado
    }

    @Test
    public void deveLancarExcecaoQuandoDeletarUsuarioNaoEncontrado() {
        Mockito.when(usuarioRepository.existsById(defaultId)).thenReturn(false);
        Assertions.assertThrows(RuntimeException.class, () -> {
            usuarioService.deletarUsuario(defaultId);
        });
        Mockito.verify(usuarioRepository).existsById(defaultId);
        Mockito.verifyNoMoreInteractions(usuarioRepository);
    }

}
