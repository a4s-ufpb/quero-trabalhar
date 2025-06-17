package com.QueroTrabalhar.services;

import com.QueroTrabalhar.dtos.user.UserDTO;
import com.QueroTrabalhar.entity.InteresseEmEmprego;
import com.QueroTrabalhar.entity.OportunidadeDeEmprego;
import com.QueroTrabalhar.entity.Usuario;
import com.QueroTrabalhar.repository.OportunidadeDeEmpregoRepository;
import com.QueroTrabalhar.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private OportunidadeDeEmpregoRepository oportunidadeRepository;


    public List<UserDTO> listarTodosUsuarios() {
        List<Usuario> users = usuarioRepository.findAll();
        return users.stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }

    public UserDTO buscarUsuarioPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        return new UserDTO(usuario);
    }

    public UserDTO salvarUsuario(UserDTO userDTO) {

        Usuario user = userDTO.userDtoToUser();

        user.getInteresseEmEmpregos().setUsuario(user);

        Usuario userSaved = usuarioRepository.save(user);

        return new UserDTO(userSaved);
    }

    public void deletarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado");
        }
        usuarioRepository.deleteById(id);
    }
    public UserDTO registrarInteresseEmOportunidadeDeEmprego(Long userId, Long oportunidadeId){

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
        if (user.getInteresseEmEmpregos() == null){
            user.setInteresseEmEmpregos(new InteresseEmEmprego());
            user.getInteresseEmEmpregos().setUsuario(user);
        }
        if (!user.getInteresseEmEmpregos().getOportunidades().contains(oportunidade)) {
            user.getInteresseEmEmpregos().adicionarOportunidadeDeEmprego(oportunidade);
        } else {
            System.out.println("A OPORTUNIDADE NÃO FOI REGISTRADA\nOportunidade já existe nos interesses do usuário");
        }

        usuarioRepository.save(user);
        return new UserDTO(user);

    }
}
