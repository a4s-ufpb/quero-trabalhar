package com.QueroTrabalhar.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tipo_de_emprego", indexes = {
        @Index(name = "idx_titulo_emprego", columnList = "titulo") // Performance de busca
})
public class TipoDeEmprego {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false)
    private boolean aprovado;

    public TipoDeEmprego(String titulo, String descricao) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.aprovado = true;
    }

    public TipoDeEmprego(String  titulo) {
        this.titulo = titulo;
        this.aprovado = false;
    }

    protected TipoDeEmprego() {}

    public Long getId() { return id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public boolean isAprovado() { return aprovado; }
    public void setAprovado(boolean aprovado)  { this.aprovado = aprovado; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TipoDeEmprego)) return false;
        TipoDeEmprego that = (TipoDeEmprego) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() { return getClass().hashCode(); }
}