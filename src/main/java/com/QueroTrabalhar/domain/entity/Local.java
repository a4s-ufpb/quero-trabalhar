package com.QueroTrabalhar.domain.entity;

import com.QueroTrabalhar.domain.enums.Pais;
import jakarta.persistence.*;

import java.util.Objects;

@Embeddable
public class Local {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ibge")
    private Cidade Cidade;

    @Enumerated(EnumType.STRING)
    @Column(length = 2)
    private Pais pais;

    public Local(Cidade Cidade, Pais pais) {
        this.Cidade = Cidade;
        this.pais = pais;
    }

    //Caso seja um pais internacional, inicializa só o país para evitar a possibilidade de pais = EUA, estado = PB, Cidade Rio Tinto
    public Local(Pais pais){
        this.Cidade = null;
        this.pais = pais;
    }

    protected Local(){}

    public Cidade getCidade() {
        return Cidade;
    }


    public Pais getPais() {
        return this.pais;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Local that)) return false;
        return Objects.equals(getCidade(), that.getCidade()) && getPais() == that.getPais();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCidade(), getPais());
    }
}
