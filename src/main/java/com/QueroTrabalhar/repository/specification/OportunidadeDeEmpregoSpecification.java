package com.QueroTrabalhar.repository.specification;

import com.QueroTrabalhar.domain.dtos.oportunidadeDeEmprego.OportunidadeDeEmpregoFilterDTO;
import com.QueroTrabalhar.domain.entity.OportunidadeDeEmprego;
import com.QueroTrabalhar.domain.enums.StatusLocalidadeFiltro;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class OportunidadeDeEmpregoSpecification {

    private OportunidadeDeEmpregoSpecification() {
    }

    public static Specification<OportunidadeDeEmprego> comFiltros(OportunidadeDeEmpregoFilterDTO filtro) {
        return (root, query, criteriaBuilder) -> {
            if (filtro == null) {
                return criteriaBuilder.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();

            String termo = normalizarTexto(filtro.termo());
            if (termo != null) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("descricao")),
                        "%" + termo.toLowerCase(Locale.ROOT) + "%"
                ));
            }

            if (filtro.tipoDeEmpregoId() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.join("tipoDeEmprego").get("id"),
                        filtro.tipoDeEmpregoId()
                ));
            }

            if (filtro.empresaId() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.join("empresa", jakarta.persistence.criteria.JoinType.LEFT).get("id"),
                        filtro.empresaId()
                ));
            }

            if (filtro.recrutadorId() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.join("perfilRecrutador").get("id"),
                        filtro.recrutadorId()
                ));
            }

            if (filtro.paisId() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("localidade").get("pais").get("id"),
                        filtro.paisId()
                ));
            }

            if (filtro.estadoId() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("localidade").get("estado").get("id"),
                        filtro.estadoId()
                ));
            }

            if (filtro.cidadeId() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("localidade").get("cidade").get("id"),
                        filtro.cidadeId()
                ));
            }

            if (filtro.modalidade() != null) {
                predicates.add(criteriaBuilder.equal(root.get("modalidade"), filtro.modalidade()));
            }

            StatusLocalidadeFiltro statusLocalidade = filtro.statusLocalidade();
            if (statusLocalidade != null) {
                switch (statusLocalidade) {
                    case VALIDADA -> predicates.add(criteriaBuilder.isNotNull(root.get("localidade").get("pais")));
                    case PENDENTE ->
                            predicates.add(criteriaBuilder.isNotNull(root.get("localidadePendente").get("id")));
                }
            }

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

    private static String normalizarTexto(String valor) {
        if (valor == null) {
            return null;
        }

        String valorNormalizado = valor.trim();
        return valorNormalizado.isEmpty() ? null : valorNormalizado;
    }
}
