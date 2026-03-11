package com.QueroTrabalhar.domain.entity;

import com.QueroTrabalhar.domain.enums.Estado;
import com.QueroTrabalhar.domain.enums.Pais;
import jakarta.persistence.*;

import java.util.Objects;

@Embeddable
public class Localizacao {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cidade_id")
    private Cidade cidade;

    @Enumerated(EnumType.STRING)
    @Column(length = 2)
    private Pais pais;

    public Localizacao(Cidade cidade, Pais pais) {
        this.cidade = cidade;
        this.pais = pais;
    }

    //Caso seja um pais internacional, inicializa só o país para evitar a possibilidade de pais = EUA, estado = PB, Cidade Rio Tinto
    public Localizacao(Pais pais){
        this.cidade = null;
        this.pais = pais;
    }

    protected Localizacao(){}

    public Cidade getCidade() {
        return cidade;
    }


    public Pais getPais() {
        return this.pais;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Localizacao that)) return false;
        return Objects.equals(getCidade(), that.getCidade()) && getPais() == that.getPais();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCidade(), getPais());
    }
}
