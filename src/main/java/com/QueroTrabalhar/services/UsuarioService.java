package com.QueroTrabalhar.services;

import com.QueroTrabalhar.domain.dtos.UserDTORequest;
import com.QueroTrabalhar.domain.dtos.UserDTOResponse;
import com.QueroTrabalhar.domain.entity.PerfilCandidato;
import com.QueroTrabalhar.domain.entity.Usuario;
import com.QueroTrabalhar.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService { // Repare que a interface UserDetailsService SUMIU!

    @Autowired
    private UsuarioRepository usuarioRepository;

    // O método loadUserByUsername FOI REMOVIDO DAQUI!
    // Ele agora vive exclusivamente na sua classe UserDetailsServiceImpl (ou AutenticacaoService).

    public List<UserDTOResponse> listarTodosUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(UserDTOResponse::new)
                .collect(Collectors.toList());
    }

    public UserDTOResponse buscarUsuarioPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado. ID: " + id));
        return new UserDTOResponse(usuario);
    }

    @Transactional
    public UserDTOResponse salvarUsuario(UserDTORequest userDTORequest, String encryptedPassword) {
        Usuario user = userDTORequest.toEntity(encryptedPassword);

        // Todo usuário nasce com perfil de candidato básico
        PerfilCandidato perfilPadrao = new PerfilCandidato(user);
        user.adicionarPerfilCandidato(perfilPadrao);

        Usuario userSaved = usuarioRepository.save(user);
        return new UserDTOResponse(userSaved);
    }

    @Transactional
    public void deletarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new EntityNotFoundException("Usuário não encontrado. ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }
}