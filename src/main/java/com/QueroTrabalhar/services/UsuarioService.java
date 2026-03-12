package com.QueroTrabalhar.services;

import com.QueroTrabalhar.domain.dtos.usuario.UsuarioRequestDTO;
import com.QueroTrabalhar.domain.dtos.usuario.UsuarioResponseDTO;
import com.QueroTrabalhar.domain.entity.PerfilCandidato;
import com.QueroTrabalhar.domain.entity.PerfilRecrutador;
import com.QueroTrabalhar.domain.entity.Usuario;
import com.QueroTrabalhar.repository.PerfilCandidatoRepository;
import com.QueroTrabalhar.repository.PerfilRecrutadorRepository;
import com.QueroTrabalhar.repository.UsuarioRepository;
import com.QueroTrabalhar.services.exceptions.DataIntegrityViolationException;
import com.QueroTrabalhar.services.exceptions.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PerfilCandidatoRepository perfilCandidatoRepository;

    @Autowired
    private PerfilRecrutadorRepository perfilRecrutadorRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    public List<UsuarioResponseDTO> listarTodosUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(UsuarioResponseDTO::daEntidade)
                .collect(Collectors.toList());
    }

    public UsuarioResponseDTO buscarUsuarioPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Usuário não encontrado. ID: " + id));
        return UsuarioResponseDTO.daEntidade(usuario);
    }


    public UsuarioResponseDTO cadastrarUsuario(UsuarioRequestDTO usuarioRequest) {
        if(usuarioRepository.existsByCpf(usuarioRequest.cpf()))
            throw new DataIntegrityViolationException("Já existe um usuário com o CPF: " + usuarioRequest.cpf());

        Usuario usuario = new Usuario(usuarioRequest);
        usuario.setSenha(encoder.encode(usuarioRequest.senha()));

        PerfilCandidato perfilPadrao = new PerfilCandidato(usuario);
        usuario.adicionarPerfilCandidato(perfilPadrao);

        return UsuarioResponseDTO.daEntidade(usuarioRepository.save(usuario));
    }

    public UsuarioResponseDTO atualizarUsuario(Long id,UsuarioRequestDTO usuarioRequest) {
        if (!usuarioRepository.existsById(id))
            throw new DataIntegrityViolationException("Não existe usuario com o id: " +id);

        Usuario usuarioAtualizado = new Usuario(
                usuarioRequest.cpf(),
                usuarioRequest.nome(),
                usuarioRequest.telefone(),
                usuarioRequest.email(),
                usuarioRequest.senha()
        );
        return UsuarioResponseDTO.daEntidade(usuarioRepository.save(usuarioAtualizado));
    }

    public void removerPerfilCandidato(Long id) {
        Usuario usuario = encontrarUsuario(id);

        if(!perfilCandidatoRepository.existsById(usuario.getId()))
            throw new ObjectNotFoundException("Perfil candidato não encontrado. ID: " + id);

        if(usuario.ehRecrutador()){
            usuario.removerPerfilCandidato();
        } else {
            throw new DataIntegrityViolationException("O usuário precisa ter pelo menos um perfil ativo");
        }
    }

    public void removerPerfilRecrutador(Long id) {
        Usuario usuario = encontrarUsuario(id);

        if(!perfilRecrutadorRepository.existsById(usuario.getId()))
            throw new ObjectNotFoundException("Perfil recrutador não encontrado. ID: " + id);

        if(usuario.ehCandidato()){
            usuario.removerPerfilRecrutador();
        } else {
            throw new DataIntegrityViolationException("O usuário precisa ter pelo menos um perfil ativo");
        }
    }

    public void adicionarPerfilCandidato(Long id) {
        Usuario usuario = encontrarUsuario(id);

        if(usuario.ehCandidato()){
            throw new DataIntegrityViolationException("O usuário já é um candidato");
        }

        PerfilCandidato perfilPadrao = new PerfilCandidato(usuario);
        usuario.adicionarPerfilCandidato(perfilPadrao);

        perfilCandidatoRepository.save(perfilPadrao);
        usuarioRepository.save(usuario);
    }

    public void adicionarPerfilRecrutador(Long id) {
        Usuario usuario = encontrarUsuario(id);

        if(usuario.ehRecrutador()){
            throw new DataIntegrityViolationException("O usuário já é um recrutador");
        }

        PerfilRecrutador perfilRecrutador = new PerfilRecrutador(usuario);
        usuario.adicionarPerfilRecrutador(perfilRecrutador);

        perfilRecrutadorRepository.save(perfilRecrutador);
        usuarioRepository.save(usuario);
    }

    private Usuario encontrarUsuario(Long id){
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Usuário não encontrado. ID: " + id));
    }

    public void deletarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ObjectNotFoundException("Usuário não encontrado. ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }
}