package com.QueroTrabalhar.entity;

import com.QueroTrabalhar.enums.Modalidade;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class OportunidadeDeEmprego {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column (nullable = false, length = 500)
    private String descricao;

    @ManyToOne
    @JoinColumn (name = "tipo_de_emprego_id")
    private TipoDeEmprego tipoDeEmprego;

    @Enumerated(EnumType.STRING)
    private Modalidade modalidade;

    @Column(nullable = false, length = 50)
    private String cidade;

    @Column (nullable = false, length = 50)
    private String estado;

    private String empresa;
}
