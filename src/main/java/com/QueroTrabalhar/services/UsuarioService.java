package com.QueroTrabalhar.services;

import com.QueroTrabalhar.domain.dtos.usuario.UsuarioRequestDTO;
import com.QueroTrabalhar.domain.dtos.usuario.UsuarioResponseDTO;
import com.QueroTrabalhar.domain.entity.OportunidadeDeEmprego;
import com.QueroTrabalhar.domain.entity.PerfilCandidato;
import com.QueroTrabalhar.domain.entity.PerfilRecrutador;
import com.QueroTrabalhar.domain.entity.Usuario;
import com.QueroTrabalhar.repository.OportunidadeDeEmpregoRepository;
import com.QueroTrabalhar.repository.PerfilCandidatoRepository;
import com.QueroTrabalhar.repository.PerfilRecrutadorRepository;
import com.QueroTrabalhar.repository.UsuarioRepository;
import com.QueroTrabalhar.services.exceptions.DataIntegrityViolationException;
import com.QueroTrabalhar.services.exceptions.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private OportunidadeDeEmpregoRepository oportunidadeRepository;

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
        Usuario usuarioExistente = encontrarUsuario(id);

        usuarioExistente.setSenha(usuarioRequest.senha());
        usuarioExistente.setNome(usuarioRequest.nome());
        usuarioExistente.setTelefone(usuarioRequest.telefone());
        usuarioExistente.setEmail(usuarioRequest.email());
        usuarioExistente.setSenha(usuarioRequest.senha());

        return UsuarioResponseDTO.daEntidade(usuarioRepository.save(usuarioExistente));
    }

    public void removerMeuPerfilCandidato(){
        String emailLogado = SecurityContextHolder.getContext().getAuthentication().getName();

        Usuario usuario = usuarioRepository.findByEmail(emailLogado).orElseThrow(() -> new ObjectNotFoundException("Usuário não encontrado no contexto de segurança."));

        processarRemocaoCandidato(usuario);
    }


    //Usado por admins ou superAdmins
    public void removerPerfilCandidatoPorId(Long id) {
        Usuario usuario = encontrarUsuario(id);

        processarRemocaoCandidato(usuario);
    }

    private void processarRemocaoCandidato(Usuario usuario) {
        if(!perfilCandidatoRepository.existsById(usuario.getId())) {
            throw new ObjectNotFoundException("Perfil candidato não encontrado para o usuário ID: " + usuario.getId());
        }

        if(usuario.ehRecrutador()) {
            usuario.removerPerfilCandidato();
            usuarioRepository.save(usuario);
        } else {
            throw new DataIntegrityViolationException("O usuário precisa ter pelo menos um perfil ativo.");
        }
    }

    @Transactional
    public void removerMeuPerfilRecrutador() {
        String emailLogado = SecurityContextHolder.getContext().getAuthentication().getName();

        Usuario usuario = usuarioRepository.findByEmail(emailLogado).orElseThrow(() -> new ObjectNotFoundException("Usuário não encontrado no contexto de segurança."));

        processarRemocaoRecrutador(usuario);
    }

    @Transactional
    public void removerPerfilRecrutadorPorId(Long id) {
        Usuario usuario = encontrarUsuario(id);

        processarRemocaoRecrutador(usuario);
    }

    private void processarRemocaoRecrutador(Usuario usuario) {
        if(!perfilRecrutadorRepository.existsById(usuario.getId()))
            throw new ObjectNotFoundException("Perfil recrutador não encontrado para o usuario ID: " + usuario.getId());

        if(usuario.ehCandidato()){
            PerfilRecrutador perfil = usuario.getPerfilRecrutador();

            if (!perfil.getOportunidadesPostadas().isEmpty()) {
                for (OportunidadeDeEmprego vaga : perfil.getOportunidadesPostadas()) {
                    oportunidadeRepository.removerTodosInteressesDaVaga(vaga.getId());
                }
            }

            usuario.removerPerfilRecrutador();
            usuarioRepository.save(usuario);
        } else {
            throw new DataIntegrityViolationException("O usuário precisa ter pelo menos um perfil ativo");
        }
    }

    public void adicionarMeuPerfilCandidato(){
        String emailLogado = SecurityContextHolder.getContext().getAuthentication().getName();

        Usuario usuario = usuarioRepository.findByEmail(emailLogado)
                .orElseThrow(() -> new ObjectNotFoundException("Usuário não encontrado no contexto de segurança."));

        processarAdicionarCandidato(usuario);
    }

    public void adicionarPerfilCandidatoPorId(Long id) {
        Usuario usuario = encontrarUsuario(id);

        processarAdicionarCandidato(usuario);
    }

    private void processarAdicionarCandidato(Usuario usuario) {
        if(usuario.ehCandidato()){
            throw new DataIntegrityViolationException("O usuário já é um candidato");
        }

        PerfilCandidato perfilPadrao = new PerfilCandidato(usuario);
        usuario.adicionarPerfilCandidato(perfilPadrao);

        perfilCandidatoRepository.save(perfilPadrao);
        usuarioRepository.save(usuario);
    }

    public void adicioncarMeuPerfilRecrutador(String nomeDaEmpresa) {
        String emailLogado = SecurityContextHolder.getContext().getAuthentication().getName();

        Usuario usuario = usuarioRepository.findByEmail(emailLogado)
                .orElseThrow(() -> new ObjectNotFoundException("Usuário não encontrado no contexto de segurança."));

        processarAdicionarRecrutador(usuario, nomeDaEmpresa);
    }

    public void adicionarPerfilRecrutadorPorId(Long id, String nomeDaEmpresa) {
        Usuario usuario = encontrarUsuario(id);

        processarAdicionarRecrutador(usuario, nomeDaEmpresa);
    }

    private void processarAdicionarRecrutador(Usuario usuario, String nomeDaEmpresa) {
        if(usuario.ehRecrutador()){
            throw new DataIntegrityViolationException("O usuário já é um recrutador");
        }

        PerfilRecrutador perfilRecrutador = new PerfilRecrutador(usuario, nomeDaEmpresa);
        usuario.adicionarPerfilRecrutador(perfilRecrutador);

        perfilRecrutadorRepository.save(perfilRecrutador);
        usuarioRepository.save(usuario);
    }

    public void meRemover(){
        String emailLogado = SecurityContextHolder.getContext().getAuthentication().getName();

        Usuario usuario = usuarioRepository.findByEmail(emailLogado)
                .orElseThrow(() -> new ObjectNotFoundException("Usuário não encontrado no contexto de segurança."));

        processarRemocaoUsuario(usuario);
    }

    public void deletarUsuarioPorId(Long id) {
        Usuario usuario = encontrarUsuario(id);

        processarRemocaoUsuario(usuario);
    }

    private void processarRemocaoUsuario(Usuario usuario) {
        usuarioRepository.delete(usuario);
    }

    private Usuario encontrarUsuario(Long id){
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Usuário não encontrado. ID: " + id));
    }
}