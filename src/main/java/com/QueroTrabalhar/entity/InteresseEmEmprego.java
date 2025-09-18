package com.QueroTrabalhar.entity;

// classe que o usuário terá, nela ele poderá indicar os interesses que ele tem, e suas exigências.

import com.QueroTrabalhar.enums.Modalidade;
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

    private List<String> cidadesDeInteresse;

    private List<String> estadosDeInteresse;

    private List<String> paisesDeInteresse;
}
