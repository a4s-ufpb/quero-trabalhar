package com.QeuroTrabalhar.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Entity //Indica que esta classe será mapeada para uma tabela no banco
@Getter @Setter  // Cria automaticamente os métodos get e set
@NoArgsConstructor @AllArgsConstructor // Cria construtores padrão e com argumentos
public class UsuarioRecrutador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Email(message = "Email deve ser válido")
    @NotNull(message = "Email é Obrigatório")
    private String email;

    @Size(min = 10, max = 15, message = "Telefone deve ter entre 10 e 15 caracteres.")
    private String telefone;

    @Column(nullable = false, length = 100)
    private String Empresas;

    @OneToMany (mappedBy = "usuarioRecrutador", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonBackReference
    private List<InteresseEmEmprego> interesseEmEmpregos;

}
