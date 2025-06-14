package com.QueroTrabalhar.dtos.user;

import com.QueroTrabalhar.entity.ExperienciaProfissional;
import com.QueroTrabalhar.entity.InteresseEmEmprego;
import com.QueroTrabalhar.entity.Usuario;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class UserDTO {

    private Long id;
    private Long cpf;
    private String nome;
    private String telefone;
    private String email;
    private List<ExperienciaProfissional> experienciaProfissionals;
    private List<InteresseEmEmprego> interesseEmEmpregos;

    public UserDTO() {}

    public Usuario userDtoToUser() {
        return new Usuario(
                id,
                cpf,
                nome,
                telefone,
                email,
                experienciaProfissionals = new ArrayList<>(),
                interesseEmEmpregos = new ArrayList<>());
    }

    public UserDTO(Usuario user) {
        this.id = user.getId();
        this.cpf = user.getCpf();
        this.nome = user.getNome();
        this.telefone = user.getTelefone();
        this.email = user.getEmail();
        this.experienciaProfissionals = user.getExperienciaProfissionais();
        this.interesseEmEmpregos = user.getInteresseEmEmpregos();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCpf() {
        return cpf;
    }

    public void setCpf(Long cpf) {
        this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<ExperienciaProfissional> getExperienciaProfissionals() {
        return experienciaProfissionals;
    }

    public void setExperienciaProfissionals(List<ExperienciaProfissional> experienciaProfissionals) {
        this.experienciaProfissionals = experienciaProfissionals;
    }

    public List<InteresseEmEmprego> getInteresseEmEmpregos() {
        return interesseEmEmpregos;
    }

    public void setInteresseEmEmpregos(List<InteresseEmEmprego> interesseEmEmpregos) {
        this.interesseEmEmpregos = interesseEmEmpregos;
    }
}
