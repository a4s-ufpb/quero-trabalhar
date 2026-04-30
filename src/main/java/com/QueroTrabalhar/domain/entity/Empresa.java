package com.QueroTrabalhar.domain.entity;

import com.QueroTrabalhar.domain.entity.localidade.Localidade;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(
        name = "empresa",
        indexes = {
                @Index(name = "idx_empresa_nome", columnList = "nome")
        }
)
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(length = 1000)
    private String descricao;

    @Column(length = 255)
    private String site;

    @Column(name = "email_publico", length = 150)
    private String emailPublico;

    @Column(name = "telefone_publico", length = 20)
    private String telefonePublico;

    @Embedded
    private Localidade localidade;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean ativo;

    public Empresa(
            String nome,
            String descricao,
            String site,
            String emailPublico,
            String telefonePublico,
            Localidade localidade
    ) {
        this.nome = nome;
        this.descricao = descricao;
        this.site = site;
        this.emailPublico = emailPublico;
        this.telefonePublico = telefonePublico;
        this.localidade = localidade;
        this.ativo = true;
    }

    protected Empresa() {
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getEmailPublico() {
        return emailPublico;
    }

    public void setEmailPublico(String emailPublico) {
        this.emailPublico = emailPublico;
    }

    public String getTelefonePublico() {
        return telefonePublico;
    }

    public void setTelefonePublico(String telefonePublico) {
        this.telefonePublico = telefonePublico;
    }

    public Localidade getLocalidade() {
        return localidade;
    }

    public void setLocalidade(Localidade localidade) {
        this.localidade = localidade;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Empresa empresa)) {
            return false;
        }
        return id != null && id.equals(empresa.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
