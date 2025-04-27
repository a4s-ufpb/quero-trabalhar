package com.QeuroTrabalhar.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity //Indica que esta classe será mapeada para uma tabela no banco
@Getter @Setter  // Cria automaticamente os métodos get e set
@NoArgsConstructor @AllArgsConstructor // Cria construtores padrão e com argumentos

public class ExperienciaProfissional {
    @Id //Define chave primária
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @JsonBackReference // Impede a serialização recursiva
    private Usuario usuario;

    @ManyToOne //
    @JoinColumn(name = "tipo_de_emprego_id")
    @JsonBackReference // Impede a serialização recursiva
    private TipoDeEmprego tipoDeEmprego;

    @Column (nullable = false, length = 500)
    private String descricao; // Descrição da experiência de trabalho

    @Column (nullable = false)
    private LocalDate dataInicio; // Data de início da experiência

    @Column
    private LocalDate dataFim; // Data de fim da experiência

}
