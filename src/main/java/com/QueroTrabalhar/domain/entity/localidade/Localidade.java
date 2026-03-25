package com.QueroTrabalhar.domain.entity.localidade;

import jakarta.persistence.*;

import java.util.Objects;

@Embeddable
public class Localidade {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pais_id", nullable = false)
    private Pais pais;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_id")
    private Estado estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cidade_id")
    private Cidade cidade;

    public Localidade(Pais pais) {
        this.pais = Objects.requireNonNull(pais, "Pais é obrigatório");
    }

    public Localidade(Pais pais, Estado estado) {
        this.pais = Objects.requireNonNull(pais, "Pais é obrigatório");
        this.estado = Objects.requireNonNull(estado, "Estado é obrigatório");
        if (!estado.getPais().equals(pais)) {
            throw new IllegalArgumentException("Conflito: O estado não pertence ao país.");
        }
    }

    public Localidade(Pais pais, Estado estado, Cidade cidade) {
        this.pais = Objects.requireNonNull(pais, "Pais é obrigatório");
        this.estado = Objects.requireNonNull(estado, "Estado é obrigatório");
        this.cidade = Objects.requireNonNull(cidade, "Cidade é obrigatória");
        if (!estado.getPais().equals(pais) || !cidade.getEstado().equals(estado)) {
            throw new IllegalArgumentException("Conflito: Quebra na hierarquia geográfica.");
        }
    }

    protected Localidade() {}

    public Pais getPais() {
        return pais;
    }

    public Estado getEstado() {
        return estado;
    }

    public Cidade getCidade() {
        return cidade;
    }
}
