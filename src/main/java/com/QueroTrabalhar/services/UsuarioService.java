package com.QueroTrabalhar.services;

import com.QueroTrabalhar.dtos.user.UserDTORequest;
import com.QueroTrabalhar.dtos.user.UserDTOResponse;
import com.QueroTrabalhar.entity.InteresseEmOportunidades;
import com.QueroTrabalhar.entity.OportunidadeDeEmprego;
import com.QueroTrabalhar.entity.Usuario;
import com.QueroTrabalhar.repository.OportunidadeDeEmpregoRepository;
import com.QueroTrabalhar.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioService implements UserDetailsService {


    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private OportunidadeDeEmpregoRepository oportunidadeRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return usuarioRepository.findByEmail(email);
    }

    public List<UserDTOResponse> listarTodosUsuarios() {
        List<Usuario> users = usuarioRepository.findAll();
        return users.stream()
                .map(UserDTOResponse::new)
                .collect(Collectors.toList());
    }

    public UserDTOResponse buscarUsuarioPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        return new UserDTOResponse(usuario);
    }

    public UserDTOResponse salvarUsuario(UserDTORequest userDTORequest, String senha) {

        Usuario user = userDTORequest.toEntity(senha);

        user.getInteresseEmOportunidades().setUsuario(user);

        Usuario userSaved = usuarioRepository.save(user);

        return new UserDTOResponse(userSaved);
    }

    public void deletarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado");
        }
        usuarioRepository.deleteById(id);
    }
    public UserDTOResponse registrarInteresseEmOportunidadeDeEmprego(Long userId, Long oportunidadeId){

        // Escolhendo o Usuário
        Optional<Usuario> userOptional = usuarioRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new RuntimeException("Usuário não encontrada");
        }

        Usuario user = userOptional.get();

        // Escolhendo o Interesse
        Optional<OportunidadeDeEmprego> oportunidadeOptional = oportunidadeRepository.findById(oportunidadeId);
        if(!oportunidadeOptional.isPresent()){
            throw new RuntimeException("Oportunidade não encontrada");
        }
        OportunidadeDeEmprego oportunidade = oportunidadeOptional.get();

        // Adicionar Oportunidade a Lista de Interesses do Usuário.
        if (user.getInteresseEmOportunidades() == null){
            user.setInteresseEmOportunidades(new InteresseEmOportunidades());
            user.getInteresseEmOportunidades().setUsuario(user);
        }
        if (!user.getInteresseEmOportunidades().getOportunidades().contains(oportunidade)) {
            user.getInteresseEmOportunidades().adicionarOportunidadeDeEmprego(oportunidade);
        } else {
            System.out.println("A OPORTUNIDADE NÃO FOI REGISTRADA\nOportunidade já existe nos interesses do usuário");
        }

        usuarioRepository.save(user);
        return new UserDTOResponse(user);

    }
}
