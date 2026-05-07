package com.QueroTrabalhar.domain.entity;

import com.QueroTrabalhar.domain.entity.localidade.Localidade;
import com.QueroTrabalhar.domain.enums.Modalidade;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.AssociationOverride;
import jakarta.persistence.AssociationOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "oportunidade_de_emprego")
public class OportunidadeDeEmprego {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String descricao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_de_emprego_id", nullable = false)
    private TipoDeEmprego tipoDeEmprego;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Modalidade modalidade;

    @Embedded
    @AssociationOverrides({
            @AssociationOverride(name = "pais", joinColumns = @JoinColumn(name = "pais_id", nullable = true)),
            @AssociationOverride(name = "estado", joinColumns = @JoinColumn(name = "estado_id", nullable = true)),
            @AssociationOverride(name = "cidade", joinColumns = @JoinColumn(name = "cidade_id", nullable = true))
    })
    private Localidade localidade;

    @ManyToMany(mappedBy = "vagasDeInteresse")
    @JsonIgnore
    private Set<PerfilCandidato> candidatosInteressados = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perfil_recrutador_id", nullable = false)
    @JsonIgnore
    private PerfilRecrutador perfilRecrutador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;

    public OportunidadeDeEmprego(
            String descricao,
            TipoDeEmprego tipoDeEmprego,
            Modalidade modalidade,
            Localidade localidade,
            PerfilRecrutador perfilRecrutador
    ) {
        this(descricao, tipoDeEmprego, modalidade, localidade, perfilRecrutador, null);
    }

    public OportunidadeDeEmprego(
            String descricao,
            TipoDeEmprego tipoDeEmprego,
            Modalidade modalidade,
            Localidade localidade,
            PerfilRecrutador perfilRecrutador,
            Empresa empresa
    ) {
        this.descricao = descricao;
        this.tipoDeEmprego = tipoDeEmprego;
        this.modalidade = modalidade;
        this.perfilRecrutador = perfilRecrutador;
        this.empresa = empresa;

        if (localidade != null) {
            definirLocalidadeValidada(localidade);
        }
    }

    protected OportunidadeDeEmprego() {
    }

    public Long getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public TipoDeEmprego getTipoDeEmprego() {
        return tipoDeEmprego;
    }

    public void setTipoDeEmprego(TipoDeEmprego tipoDeEmprego) {
        this.tipoDeEmprego = tipoDeEmprego;
    }

    public Modalidade getModalidade() {
        return modalidade;
    }

    public void setModalidade(Modalidade modalidade) {
        this.modalidade = modalidade;
    }

    public Localidade getLocalizacao() {
        return localidade;
    }

    public void setLocalizacao(Localidade localidade) {
        if (localidade != null) {
            definirLocalidadeValidada(localidade);
            return;
        }

        this.localidade = null;
    }

    public void definirLocalidadeValidada(Localidade localidade) {
        this.localidade = Objects.requireNonNull(localidade, "A localidade validada \u00E9 obrigat\u00F3ria.");
    }

    public Set<PerfilCandidato> getCandidatosInteressados() {
        return Collections.unmodifiableSet(candidatosInteressados);
    }

    public void adicionarInteressado(PerfilCandidato perfilCandidato) {
        this.candidatosInteressados.add(perfilCandidato);
        perfilCandidato.demonstrarInteresse(this);
    }

    public void removerInteressado(PerfilCandidato perfilCandidato) {
        this.candidatosInteressados.remove(perfilCandidato);
        perfilCandidato.removerInteresse(this);
    }

    public PerfilRecrutador getPerfilRecrutador() {
        return perfilRecrutador;
    }

    public void setPerfilRecrutador(PerfilRecrutador perfilRecrutador) {
        this.perfilRecrutador = perfilRecrutador;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OportunidadeDeEmprego that)) {
            return false;
        }
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
