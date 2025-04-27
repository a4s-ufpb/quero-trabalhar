package com.QeuroTrabalhar.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    @JsonBackReference // Impede a serialização recursiva
    private TipoDeEmprego tipoDeEmprego;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonBackReference // Impede a serialização recursiva
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "usuarioRecrutador_id", nullable = false)
    @JsonBackReference // Impede a serialização recursiva
    private UsuarioRecrutador usuarioRecrutador;

}
