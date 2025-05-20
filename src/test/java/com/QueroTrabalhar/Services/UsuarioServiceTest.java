package com.QueroTrabalhar.Services;

import com.QueroTrabalhar.DTOs.UsuarioDTO;
import com.QueroTrabalhar.Entity.Usuario;
import com.QueroTrabalhar.Repository.UsuarioRepository;
import org.junit.jupiter.api.Assertions;
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

    @Test
    public void deveSalvarUsuario() {

        UsuarioDTO.Request usuario = new UsuarioDTO.Request();
        usuario.setNome("Fernanda");
        usuario.setCpf(12345678912L);
        usuario.setTelefone("1234567890");
        usuario.setEmail("fernanda@gamail.com");

        Usuario usuariomock = new Usuario();
        usuariomock.setId(1L);
        usuariomock.setNome("Fernanda");

        Mockito.when(usuarioRepository.save(Mockito.any(Usuario.class))).thenReturn(usuariomock);

      UsuarioDTO.Response usuarioSalvo = usuarioService.salvarUsuario(usuario);

        Assertions.assertNotNull(usuarioSalvo.getId());
        Assertions.assertEquals("Fernanda", usuarioSalvo.getNome());
    }

    @Test
    public void deveBuscarPorId() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Fernanda");

        Mockito.when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        UsuarioDTO.Response resultado = usuarioService.buscarUsuarioPorId(1L);

        Assertions.assertNotNull(resultado);
        Assertions.assertEquals("Fernanda", resultado.getNome());
    }

    @Test
    public void deveRetornarListaDeUsuarios() {

        // Cria usuários mockados com IDs e dados necessários
        Usuario usuario1 = new Usuario();
        usuario1.setId(1L); // Define o ID
        usuario1.setNome("Gustavo");

        Usuario usuario2 = new Usuario();
        usuario2.setId(2L); // Define o ID
        usuario2.setNome("Vitória");


        List<Usuario> usuariosMock = Arrays.asList(usuario1, usuario2);


        Mockito.when(usuarioRepository.findAll()).thenReturn(usuariosMock);


        List<UsuarioDTO.Response> resultado = usuarioService.listarTodosUsuarios();

        Assertions.assertFalse(resultado.isEmpty());
        Assertions.assertEquals(2, resultado.size());
    }

    @Test
    public void deveDeletarUsuario() {
        Long usuarioID = 1L;
        Mockito.doNothing().when(usuarioRepository).deleteById(usuarioID);

        usuarioService.deletarUsuario(usuarioID);

        Mockito.verify(usuarioRepository).deleteById(usuarioID); // Verifica se deleteById foi chamado
    }

    @Test
    public void deveConverterEntidadeParaDTO() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Fernanda");

        UsuarioDTO.Response usuarioDTO = usuarioService.converterParaDto(usuario);

        Assertions.assertNotNull(usuarioDTO);
        Assertions.assertEquals("Fernanda", usuarioDTO.getNome());
        Assertions.assertEquals(1L, usuarioDTO.getId());
    }

    @Test
    public void deveConverterDtoParaEntidade(){
        UsuarioDTO.Request usuarioDTO = new UsuarioDTO.Request();
        usuarioDTO.setCpf(12345678987L);
        usuarioDTO.setNome("Fernanda");
        usuarioDTO.setTelefone("1234567890");
        usuarioDTO.setEmail("fernanda@email.com");

        Usuario usuario = usuarioService.converterParaEntidade(usuarioDTO);

        Assertions.assertNotNull(usuario);
        Assertions.assertEquals("Fernanda", usuario.getNome());
        Assertions.assertEquals(12345678987L, usuario.getCpf());
        Assertions.assertEquals("1234567890", usuario.getTelefone());
        Assertions.assertEquals("fernanda@email.com", usuario.getEmail());
    }
}
