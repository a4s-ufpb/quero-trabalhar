package com.QueroTrabalhar.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class InteresseEmOportunidades {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    @JsonBackReference
    @ToString.Exclude
    private Usuario usuario;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "interesseId")
    private List<OportunidadeDeEmprego> oportunidades = new ArrayList<>();

    // Pode manter seu método utilitário, ele é útil
    public void adicionarOportunidadeDeEmprego(OportunidadeDeEmprego oportunidade) {
        if (this.oportunidades == null) {
            this.oportunidades = new ArrayList<>();
        }
        this.oportunidades.add(oportunidade);
    }
}