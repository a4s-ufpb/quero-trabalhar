package com.QueroTrabalhar.domain.entity;

// classe que o usuário terá, nela ele poderá indicar os interesses que ele tem, e suas exigências.

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class InteresseEmEmprego {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<TipoDeEmprego> tipoDeEmprego;

    private boolean querTrabalharRemoto;

    private boolean temRestricaoDeLugar;

    @ElementCollection
    private List<String> cidadesDeInteresse;

    @ElementCollection
    private List<String> estadosDeInteresse;

    @ElementCollection
    private List<String> paisesDeInteresse;
}
