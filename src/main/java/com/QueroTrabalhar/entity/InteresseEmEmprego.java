package com.QueroTrabalhar.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity //Indica que esta classe será mapeada para uma tabela no banco
@Getter @Setter // Cria automaticamente os métodos get e set
@NoArgsConstructor @AllArgsConstructor  // Cria construtores padrão e com argumentos

public class InteresseEmEmprego {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tipo_de_emprego_id", nullable = false)

    private TipoDeEmprego tipoDeEmprego;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)

    private Usuario usuario;

}
