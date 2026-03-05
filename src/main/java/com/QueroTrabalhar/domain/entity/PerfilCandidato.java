package com.QueroTrabalhar.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "perfil_candidato")
public class PerfilCandidato {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "usuario_id")
    @JsonIgnore
    private Usuario usuario;

    @OneToMany(mappedBy = "perfilCandidato", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExperienciaProfissional> experiencias = new ArrayList<>();

    @OneToOne(mappedBy = "perfilCandidato", cascade = CascadeType.ALL, orphanRemoval = true)
    private InteresseEmEmprego interesseEmEmprego;

    @ManyToMany
    @JoinTable(
            name = "candidato_vaga_interesse",
            joinColumns = @JoinColumn(name = "perfil_candidato_id"),
            inverseJoinColumns = @JoinColumn(name = "oportunidade_id")
    )
    private Set<OportunidadeDeEmprego> vagasDeInteresse = new HashSet<>();

    protected PerfilCandidato() {}

    public PerfilCandidato(Usuario usuario) {
        this.usuario = usuario;
    }

    public Long getId() { return id; }
    public Usuario getUsuario() { return usuario; }

    void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public List<ExperienciaProfissional> getExperiencias() {
        return Collections.unmodifiableList(this.experiencias);
    }

    public void adicionarExperiencia(ExperienciaProfissional exp) {
        this.experiencias.add(exp);
        exp.setPerfilCandidato(this); // Lembre-se de atualizar isso na entidade ExperienciaProfissional!
    }

    public void removerExperiencia(ExperienciaProfissional exp) {
        this.experiencias.remove(exp);
        exp.setPerfilCandidato(null);
    }

    public InteresseEmEmprego getInteresseEmEmprego() { return interesseEmEmprego; }

    public void definirInteresse(InteresseEmEmprego interesse) {
        this.interesseEmEmprego = interesse;
        interesse.setPerfilCandidato(this);
    }

    public void removerInteresseEmEmprego() {
        if (this.interesseEmEmprego != null) {
            this.interesseEmEmprego.setPerfilCandidato(null);
            this.interesseEmEmprego = null;
        }
    }

    public Set<OportunidadeDeEmprego> getVagasDeInteresse() {
        return Collections.unmodifiableSet(this.vagasDeInteresse);
    }

    public void demonstrarInteresse(OportunidadeDeEmprego vaga) {
        this.vagasDeInteresse.add(vaga);
    }

    public void removerInteresse(OportunidadeDeEmprego vaga) {
        this.vagasDeInteresse.remove(vaga);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PerfilCandidato)) return false;
        PerfilCandidato that = (PerfilCandidato) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}