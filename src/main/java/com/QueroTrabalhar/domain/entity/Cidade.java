package com.QueroTrabalhar.domain.entity;

import com.QueroTrabalhar.domain.enums.Estado;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "cidade", indexes = {@Index(columnList = "estado")})
public class Cidade {

    @Id
    private Long idIbge; // Usaremos o código oficial como ID primário

    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(length = 2, nullable = false)
    private Estado estado;

    public Cidade(Long idIbge, String nome, Estado estado) {
        this.idIbge = idIbge;
        this.nome = nome;
        this.estado = estado;
    }

    protected Cidade() {}

    public Long getIdIbge() {
        return idIbge;
    }

    public String getNome() {
        return nome;
    }

    public Estado getEstado() {
        return estado;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Cidade cidade)) return false;
        return Objects.equals(getIdIbge(), cidade.getIdIbge());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getIdIbge());
    }
}
