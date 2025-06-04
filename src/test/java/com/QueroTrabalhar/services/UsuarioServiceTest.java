
package com.QueroTrabalhar.services;

import com.QueroTrabalhar.dtos.user.UserRequestDTO;
import com.QueroTrabalhar.dtos.user.UserResponseDTO;
import com.QueroTrabalhar.entity.Usuario;
import com.QueroTrabalhar.repository.UsuarioRepository;
import com.QueroTrabalhar.util.UserConverter;
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

    @Mock
    private UserConverter userConverter;

    @InjectMocks
    private UsuarioService usuarioService;

    // Atributos dos objetos
    private Long defaultId;
    private String defaultName;
    private Long defaultCpf;
    private String defaultPhone;
    private String defaultEmail;
    private List<Long> defaultExperiencias;
    private List<Long> defaultInteresses;

    // Objetos para teste
    private Usuario user;
    private UserResponseDTO userResponseDTO;
    private UserRequestDTO userRequestDTO;

    // objetos para o teste de lista
    private Usuario user1;
    private Usuario user2;
    private UserResponseDTO responseDTOToUser1;
    private UserResponseDTO responseDTOToUser2;


    @BeforeEach
    void setUp() {
        // --- Configurações para salvar/buscar por ID ---
        defaultId = 1L;
        defaultName = "Fernanda";
        defaultCpf = 12345678964L;
        defaultPhone = "83912345678";
        defaultEmail = "fernanda23@email.com";
        defaultExperiencias = new ArrayList<>();
        defaultInteresses = new ArrayList<>();

        // Configura o UserRequest, os dados de entrada.
        userRequestDTO = new UserRequestDTO(
                defaultCpf,
                defaultName,
                defaultPhone,
                defaultEmail);

        // Configura um User
        user = new Usuario(
                defaultId,
                defaultCpf,
                defaultName,
                defaultPhone,
                defaultEmail);

        // Configura um UserResponseDTO
        userResponseDTO = new UserResponseDTO(
                defaultId,
                defaultCpf,
                defaultName,
                defaultPhone,
                defaultEmail,
                defaultExperiencias,
                defaultInteresses);

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

        // DTO de Resposta para Usuario 1
        responseDTOToUser1 = new UserResponseDTO(
                user1.getId(),
                user1.getCpf(),
                user1.getNome(),
                user1.getTelefone(),
                user1.getEmail(),
                defaultExperiencias,
                defaultInteresses);

        // DTO de Resposta para Usuario 2
        responseDTOToUser2 = new UserResponseDTO(
                user2.getId(),
                user2.getCpf(),
                user2.getNome(),
                user2.getTelefone(),
                user2.getEmail(),
                defaultExperiencias,
                defaultInteresses);
    }



    @Test
    public void deveSalvarUsuarioComSucesso() {

        Mockito.when(userConverter.userDtoToEntity(Mockito.any(UserRequestDTO.class))).thenReturn(user);

        Mockito.when(userConverter.userEntityToDto(Mockito.any(Usuario.class))).thenReturn(userResponseDTO);

        Mockito.when(usuarioRepository.save(Mockito.any(Usuario.class))).thenReturn(user);


        UserResponseDTO userSaved = usuarioService.salvarUsuario(userRequestDTO);


        Assertions.assertNotNull(userSaved.getId());
        Assertions.assertEquals("Fernanda", userSaved.getNome());
    }

    @Test
    public void deveBuscarPorIdComSucesso() {

        Mockito.when(usuarioRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(userConverter.userEntityToDto(user)).thenReturn(userResponseDTO);

        UserResponseDTO resultado = usuarioService.buscarUsuarioPorId(1L);

        Assertions.assertNotNull(resultado.getId());
        Assertions.assertEquals("Fernanda", resultado.getNome());
    }

    @Test
    public void deveLancarEntityNotFoundExeptionQuandoUsuarioNaoEncontrado() {

         Mockito.when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
                    usuarioService.buscarUsuarioPorId(1L);});
        Mockito.verify(usuarioRepository).findById(1L);
        Mockito.verifyNoInteractions(userConverter);
    }

    @Test
    public void deveRetornarListaDeUsuariosComSucesso() {

        List<Usuario> users = Arrays.asList(user1, user2);
        List<UserResponseDTO> usersResponseDTOs = Arrays.asList(responseDTOToUser1, responseDTOToUser2);


        Mockito.when(usuarioRepository.findAll()).thenReturn(users);
        Mockito.when(userConverter.userEntityToDto(user1)).thenReturn(responseDTOToUser1);
        Mockito.when(userConverter.userEntityToDto(user2)).thenReturn(responseDTOToUser2);


        List<UserResponseDTO> result = usuarioService.listarTodosUsuarios();

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(2, result.size());

        Assertions.assertEquals(usersResponseDTOs, result);
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
