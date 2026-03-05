package com.QueroTrabalhar.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "perfil_recrutador")
public class PerfilRecrutador {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "usuario_id")
    @JsonIgnore
    private Usuario usuario;

    //Talvez uma entidade empresa o futuro
    @Column(nullable = false, length = 100)
    private String empresa;

    @OneToMany(mappedBy = "perfilRecrutador", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<OportunidadeDeEmprego> oportunidadesPostadas = new ArrayList<>();

    public PerfilRecrutador(Usuario usuario, String empresa) {
        this.usuario = usuario;
        this.empresa = empresa;
    }

    protected PerfilRecrutador() {}

    public Long getId() { return id; }
    public Usuario getUsuario() { return usuario; }

    // Setter package-private
    void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getEmpresa() { return empresa; }
    public void setEmpresa(String empresa) { this.empresa = empresa; }

    public List<OportunidadeDeEmprego> getOportunidadesPostadas() {
        return Collections.unmodifiableList(this.oportunidadesPostadas);
    }

    public void adicionarOportunidadePostada(OportunidadeDeEmprego vaga) {
        this.oportunidadesPostadas.add(vaga);
        vaga.setPerfilRecrutador(this);
    }

    public void removerOportunidadePostada(OportunidadeDeEmprego vaga) {
        this.oportunidadesPostadas.remove(vaga);
        vaga.setPerfilRecrutador(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PerfilRecrutador)) return false;
        PerfilRecrutador that = (PerfilRecrutador) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}