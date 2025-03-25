package com.QeuroTrabalhar.Model;

import jakarta.persistence.*;
import lombok.*;

@Entity //Indica que esta classe será mapeada para uma tabela no banco
@Getter @Setter  // Cria automaticamente os métodos get e set
@NoArgsConstructor @AllArgsConstructor // Cria construtores padrão e com argumentos

public class TipoDeEmprego {

    @Id //Define chave primária
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // Faz o ID ser gerado automaticamente pelo banco (autoincrement no MySQL)

    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    // Garante que o campo não pode ser nulo e define o tamanho máximo
    private String titulo;

    @Column(columnDefinition = "TEXT")
    // Define que o campo será armazenadoo como um tipo TEXT no banco
    private String descricao;
}
