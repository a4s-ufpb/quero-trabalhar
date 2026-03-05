package com.QueroTrabalhar.domain.dtos;

import com.QueroTrabalhar.domain.entity.Usuario;
import com.QueroTrabalhar.domain.enums.Role;

import java.util.Set;

public class UserDTOResponse {

    private Long id;
    private String cpf;
    private String nome;
    private String telefone;
    private String email;

    // Substituímos a Role única pelo Set de Perfis de segurança
    private Set<Role> profiles;

    // Flags úteis para o Front-end saber o que renderizar
    private boolean ehCandidato;
    private boolean ehRecrutador;

    public UserDTOResponse() {}

    public UserDTOResponse(Usuario user) {
        this.id = user.getId();
        this.cpf = user.getCpf();
        this.nome = user.getNome();
        this.telefone = user.getTelefone();
        this.email = user.getEmail();

        // Mapeamentos atualizados de acordo com a nova Entidade Usuario
        this.profiles = user.getProfiles();
        this.ehCandidato = user.ehCandidato();
        this.ehRecrutador = user.ehRecrutador();
    }

    // --- GETTERS E SETTERS ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Set<Role> getProfiles() { return profiles; }
    public void setProfiles(Set<Role> profiles) { this.profiles = profiles; }

    public boolean isEhCandidato() { return ehCandidato; }
    public void setEhCandidato(boolean ehCandidato) { this.ehCandidato = ehCandidato; }

    public boolean isEhRecrutador() { return ehRecrutador; }
    public void setEhRecrutador(boolean ehRecrutador) { this.ehRecrutador = ehRecrutador; }
}