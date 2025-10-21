package com.QueroTrabalhar.dtos.user;

import com.QueroTrabalhar.entity.ExperienciaProfissional;
import com.QueroTrabalhar.entity.InteresseEmOportunidades;
import com.QueroTrabalhar.entity.Usuario;
import com.QueroTrabalhar.enums.Role;

import java.util.ArrayList;
import java.util.List;

public class UserDTORequest {
    private Long id;
    private Long cpf;
    private String nome;
    private String telefone;
    private String email;
    private String senha;
    private Role role;
    private List<ExperienciaProfissional> experienciaProfissionals;
    private InteresseEmOportunidades interesseEmEmpregos;


    public Usuario toEntity(String encryptedPassword) {
        Usuario user = new Usuario();
        user.setId(this.id);
        user.setCpf(this.cpf);
        user.setNome(this.nome);
        user.setTelefone(this.telefone);
        user.setEmail(this.email);
        user.setSenha(encryptedPassword);
        user.setRole(this.role != null ? this.role : Role.USER);
        user.setExperienciaProfissionais(experienciaProfissionals);
        user.setInteresseEmOportunidades(interesseEmEmpregos);

        if (user.getExperienciaProfissionais() == null) { user.setExperienciaProfissionais(new ArrayList<>()); }
        if (user.getInteresseEmOportunidades() == null) { user.setInteresseEmOportunidades(new InteresseEmOportunidades());}


        return user;
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

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
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
}
