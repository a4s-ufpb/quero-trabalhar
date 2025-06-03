package com.QueroTrabalhar.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity //Indica que esta classe será mapeada para uma tabela no banco
@Getter @Setter  // Cria automaticamente os métodos get e set
@NoArgsConstructor @AllArgsConstructor // Cria construtores padrão e com argumentos

public class Indicacoes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String autor;

    private String mensagem;

}
