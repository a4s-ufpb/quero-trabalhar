package com.QueroTrabalhar.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity //Indica que esta classe será mapeada para uma tabela no banco
@Getter @Setter // Cria automaticamente os métodos get e set
@AllArgsConstructor  // Cria construtores padrão e com argumentos

public class InteresseEmOportunidades {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id",nullable = false,unique = true)
    @JsonBackReference
    private Usuario usuario;

    @OneToMany ( cascade = CascadeType.ALL)
    @JoinColumn( name = "interesseId")
    private List<OportunidadeDeEmprego> oportunidades;

    public InteresseEmOportunidades() {
        this.oportunidades = new ArrayList<>();
    }


    public void adicionarOportunidadeDeEmprego(OportunidadeDeEmprego oportunidade) {
        if (oportunidades == null) {
            oportunidades = new ArrayList<>();
        }
        oportunidades.add(oportunidade);
    }



}
