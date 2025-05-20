package com.QueroTrabalhar.Services;

import com.QueroTrabalhar.DTOs.UsuarioDTO;
import com.QueroTrabalhar.Entity.ExperienciaProfissional;
import com.QueroTrabalhar.Entity.InteresseEmEmprego;
import com.QueroTrabalhar.Entity.Usuario;
import com.QueroTrabalhar.Repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.parser.Entity;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;


    public List<UsuarioDTO.Response> listarTodosUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();

        return usuarios.stream()
                .map(this::converterParaDto)
                .collect(Collectors.toList());
    }

    public UsuarioDTO.Response buscarUsuarioPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        return converterParaDto(usuario);
    }

    public UsuarioDTO.Response salvarUsuario(UsuarioDTO.Request dto) {
        Usuario usuario = converterParaEntidade(dto);
        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        return converterParaDto(usuarioSalvo);
    }

    public void deletarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

    public UsuarioDTO.Response converterParaDto(Usuario usuario) {
        UsuarioDTO.Response dto = new UsuarioDTO.Response();
        dto.setId(usuario.getId());
        dto.setCpf(usuario.getCpf());
        dto.setNome(usuario.getNome());
        dto.setTelefone(usuario.getTelefone());
        dto.setEmail(usuario.getEmail());

        // Populando lista de IDs dos relacionamentos
        if (usuario.getExperienciaProfissionais() != null) {
            List<Long> experienciaIds = usuario.getExperienciaProfissionais()
                    .stream()
                    .map(ExperienciaProfissional::getId)  // pega o id de cada experiência
                    .collect(Collectors.toList());

            dto.setExperienciaProfissionaisIds(experienciaIds);
        }

        if (usuario.getInteresseEmEmpregos() != null) {
            List<Long> interesseIds = usuario.getInteresseEmEmpregos()
                    .stream()
                    .map(InteresseEmEmprego::getId)  // pega o id de cada interesse
                    .collect(Collectors.toList());

            dto.setInteresseEmEmpregosIds(interesseIds);
        }

        return dto;
    }

    public Usuario converterParaEntidade(UsuarioDTO.Request dto) {
        Usuario usuario = new Usuario();
        usuario.setCpf(dto.getCpf());
        usuario.setNome(dto.getNome());
        usuario.setTelefone(dto.getTelefone());
        usuario.setEmail(dto.getEmail());
        return usuario;
    }


}
