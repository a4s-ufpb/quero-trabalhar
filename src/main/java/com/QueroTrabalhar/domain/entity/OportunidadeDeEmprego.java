package com.QueroTrabalhar.domain.entity;

import com.QueroTrabalhar.domain.enums.Modalidade;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

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
    private Localizacao localizacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perfil_recrutador_id", nullable = false)
    @JsonIgnore
    private PerfilRecrutador perfilRecrutador;

    public OportunidadeDeEmprego(String descricao, TipoDeEmprego tipoDeEmprego, Modalidade modalidade, Localizacao localizacao, PerfilRecrutador perfilRecrutador) {
        this.descricao = descricao;
        this.tipoDeEmprego = tipoDeEmprego;
        this.modalidade = modalidade;
        this.localizacao = localizacao;
        this.perfilRecrutador = perfilRecrutador;
    }

    protected OportunidadeDeEmprego() {}

    public Long getId() { return id; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public TipoDeEmprego getTipoDeEmprego() { return tipoDeEmprego; }
    public void setTipoDeEmprego(TipoDeEmprego tipoDeEmprego) { this.tipoDeEmprego = tipoDeEmprego; }

    public Modalidade getModalidade() { return modalidade; }
    public void setModalidade(Modalidade modalidade) { this.modalidade = modalidade; }

    public Localizacao getLocalizacao() { return localizacao; }
    // Ao mudar de cidade, o usuário envia um novo objeto Localizacao inteiro (Imutabilidade!)
    public void setLocalizacao(Localizacao localizacao) { this.localizacao = localizacao; }

    public PerfilRecrutador getPerfilRecrutador() { return perfilRecrutador; }
    public void setPerfilRecrutador(PerfilRecrutador perfilRecrutador) { this.perfilRecrutador = perfilRecrutador; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OportunidadeDeEmprego)) return false;
        OportunidadeDeEmprego that = (OportunidadeDeEmprego) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}