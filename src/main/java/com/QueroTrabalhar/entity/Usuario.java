package com.QueroTrabalhar.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity //Indica que esta classe será mapeada para uma tabela no banco
@Getter @Setter  // Cria automaticamente os métodos get e set
@NoArgsConstructor @AllArgsConstructor // Cria construtores padrão e com argumentos

public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 11)
    private Long cpf;

    @Column(nullable = false, length = 100)
    private String nome;

    @Size(min = 10, max = 15, message = "Telefone deve ter entre 10 e 15 caracteres.")
    private String telefone;

    @Email(message = "Email deve ser válido")
    @NotNull(message = "Email é Obrigatório")
    private String email;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ExperienciaProfissional> experienciaProfissionais;

    @OneToMany (mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InteresseEmEmprego> interesseEmEmpregos;


    public Usuario(Long id, Long cpf, String nome, String telefone, String email) {
        this.id = id;
        this.cpf = cpf;
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
        this.experienciaProfissionais = new ArrayList<>();
        this.interesseEmEmpregos = new ArrayList<>();
    }

}
