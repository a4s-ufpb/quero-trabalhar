package com.QueroTrabalhar.domain.dtos;

import com.QueroTrabalhar.domain.entity.Usuario;

public class UserDTORequest {

    // Removido o ID (Não faz sentido enviar ID num Request de criação)
    private String cpf;
    private String nome;
    private String telefone;
    private String email;
    private String senha;

    // Removidas as listas de Experiência, Interesses e Roles.
    // Isso agora é responsabilidade dos Perfis!

    public Usuario toEntity(String encryptedPassword) {
        // Usamos o construtor limpo que criamos na entidade Usuario.
        // Ele já adiciona a Role.USER por padrão lá dentro.
        return new Usuario(
                this.cpf,
                this.nome,
                this.telefone,
                this.email,
                encryptedPassword
        );
    }

    // --- GETTERS E SETTERS ---

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
}