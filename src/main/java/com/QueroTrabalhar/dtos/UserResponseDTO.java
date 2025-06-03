package com.QueroTrabalhar.dtos;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class UserResponseDTO {

    private Long id;
    private Long cpf;
    private String nome;
    private String telefone;
    private String email;

    private List<Long> experienciaProfissionalIds;
    private List<Long> interesseEmEmpregoIds;

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

    public List<Long> getExperienciaProfissionalIds() {
        return experienciaProfissionalIds;
    }

    public void setExperienciaProfissionalIds(List<Long> experienciaProfissionalIds) {
        this.experienciaProfissionalIds = experienciaProfissionalIds;
    }

    public List<Long> getInteresseEmEmpregoIds() {
        return interesseEmEmpregoIds;
    }

    public void setInteresseEmEmpregoIds(List<Long> interesseEmEmpregoIds) {
        this.interesseEmEmpregoIds = interesseEmEmpregoIds;
    }
}
