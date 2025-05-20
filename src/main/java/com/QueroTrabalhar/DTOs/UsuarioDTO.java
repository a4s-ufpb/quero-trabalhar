package com.QueroTrabalhar.DTOs;

import com.QueroTrabalhar.Entity.Usuario;

import java.util.List;

public class UsuarioDTO {

    public static class Request{
        private long cpf;
        private String nome;
        private String telefone;
        private String email;


        // Getters and Setters

        public long getCpf() {return cpf;}
        public void setCpf(long cpf) {this.cpf = cpf;}

        public String getNome() {return nome;}
        public void setNome(String nome) {this.nome = nome;}

        public String getTelefone() {return telefone;}
        public void setTelefone(String telefone) {this.telefone = telefone;}

        public String getEmail() {return email;}
        public void setEmail(String email) {this.email = email;}
    }

    public static class Response{
        private Long id;
        private Long cpf;
        private String nome;
        private String telefone;
        private String email;

        // Relacionamentos
        private List<Long> experienciaProfissionaisIds;
        private List<Long> interesseEmEmpregosIds;

        // Getters and Setters

        public long getId() {return id;}
        public void setId(long id) {this.id = id;}

        public Long getCpf() {return cpf;}
        public void setCpf(Long cpf) {this.cpf = cpf;}

        public String getNome() {return nome;}
        public void setNome(String nome) {this.nome = nome;}

        public String getTelefone() {return telefone;}
        public void setTelefone(String telefone) {this.telefone = telefone;}

        public String getEmail() {return email;}
        public void setEmail(String email) {this.email = email;}


        public List<Long> getExperienciaProfissionaisIds() {return experienciaProfissionaisIds;}
        public void setExperienciaProfissionaisIds(List<Long> ids) {this.experienciaProfissionaisIds = ids;}

        public List<Long> getInteresseEmEmpregosIds() {return interesseEmEmpregosIds;}
        public void setInteresseEmEmpregosIds(List<Long> ids) {this.interesseEmEmpregosIds = ids;}
    }
}
