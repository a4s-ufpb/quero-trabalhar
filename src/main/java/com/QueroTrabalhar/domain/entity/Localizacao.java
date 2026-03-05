package com.QueroTrabalhar.domain.entity;

import com.QueroTrabalhar.domain.enums.Estado;
import com.QueroTrabalhar.domain.enums.Pais;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.util.Objects;

@Embeddable
public class Localizacao {

    @Column(length = 50)
    private String cidade;

    @Enumerated(EnumType.STRING)
    @Column(length = 2)
    private Estado estado;

    @Enumerated(EnumType.STRING)
    @Column(length = 2)
    private Pais pais;

    public Localizacao(String cidade, Estado estado, Pais pais) {
        this.cidade = cidade;
        this.estado = estado;
        this.pais = pais;
    }

    //Caso seja um pais internacional, inicializa só o país para evitar a possibilidade de pais = EUA, estado = PB
    public Localizacao(Pais pais){
        this.cidade = null;
        this.estado = null;
        this.pais = pais;
    }

    protected Localizacao(){}

    public String getCidade() {
        return cidade;
    }

    public Estado getEstado() {
        return estado;
    }

    public Pais getPais() {
        return this.pais;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Localizacao that)) return false;
        return Objects.equals(getCidade(), that.getCidade()) && getEstado() == that.getEstado() && getPais() == that.getPais();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCidade(), getEstado(), getPais());
    }
}
