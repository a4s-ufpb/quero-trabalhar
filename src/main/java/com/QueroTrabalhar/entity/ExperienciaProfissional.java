package com.QueroTrabalhar.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity //Indica que esta classe será mapeada para uma tabela no banco
@Getter @Setter  // Cria automaticamente os métodos get e set
@NoArgsConstructor @AllArgsConstructor // Cria construtores padrão e com argumentos

public class ExperienciaProfissional {
    @Id //Define chave primária
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne //
    @JoinColumn(name = "tipo_de_emprego_id")
    private TipoDeEmprego tipoDeEmprego;

    @OneToMany
    private List<Indicacoes>  indicacoes;

    @Column (nullable = false, length = 500)
    private String descricao; // Descrição da experiência de trabalho

    @Column (nullable = false)
    private LocalDate dataInicio; // Data de início da experiência

    @Column
    private LocalDate dataFim; // Data de fim da experiência

}
