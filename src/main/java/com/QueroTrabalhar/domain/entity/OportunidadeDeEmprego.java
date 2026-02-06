package com.QueroTrabalhar.domain.entity;

import com.QueroTrabalhar.domain.enums.Modalidade;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "tipo_de_emprego_id")
    private TipoDeEmprego tipoDeEmprego;

    @Enumerated(EnumType.STRING)
    private Modalidade modalidade;

    @Column(nullable = false, length = 50)
    private String cidade;

    @Column(nullable = false, length = 50)
    private String estado;

    @ManyToOne
    @JoinColumn(name = "recrutador_id", nullable = false)
    private UsuarioRecrutador recrutador; // Agora sabemos quem é o dono da vaga
}