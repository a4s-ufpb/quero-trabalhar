package com.QueroTrabalhar.dtos.user;

import com.QueroTrabalhar.entity.ExperienciaProfissional;
import com.QueroTrabalhar.entity.InteresseEmOportunidades;
import com.QueroTrabalhar.entity.Usuario;
import com.QueroTrabalhar.enums.Role;

import java.util.List;

public class UserDTOResponse {

    private Long id;
    private Long cpf;
    private String nome;
    private String telefone;
    private String email;
    private Role role;
    private List<ExperienciaProfissional> experienciaProfissionals;
    private InteresseEmOportunidades interesseEmEmpregos;

    public UserDTOResponse() {}

    public UserDTOResponse(Usuario user) {
        this.id = user.getId();
        this.cpf = user.getCpf();
        this.nome = user.getNome();
        this.telefone = user.getTelefone();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.experienciaProfissionals = user.getExperienciaProfissionais();
        this.interesseEmEmpregos = user.getInteresseEmOportunidades();
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

    public InteresseEmOportunidades getInteresseEmEmpregos() {
        return interesseEmEmpregos;
    }

    public void setInteresseEmEmpregos(InteresseEmOportunidades interesseEmEmpregos) {
        this.interesseEmEmpregos = interesseEmEmpregos;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
