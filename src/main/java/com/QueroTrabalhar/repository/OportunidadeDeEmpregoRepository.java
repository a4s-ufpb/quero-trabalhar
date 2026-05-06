package com.QueroTrabalhar.repository;

import com.QueroTrabalhar.domain.entity.OportunidadeDeEmprego;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OportunidadeDeEmpregoRepository
        extends JpaRepository<OportunidadeDeEmprego, Long>, JpaSpecificationExecutor<OportunidadeDeEmprego> {

    List<OportunidadeDeEmprego> findByEmpresaId(Long empresaId);

    List<OportunidadeDeEmprego> findByEmpresaIdAndLocalidadePaisIsNotNull(Long empresaId);

    List<OportunidadeDeEmprego> findByPerfilRecrutadorId(Long perfilRecrutadorId);

    Optional<OportunidadeDeEmprego> findByIdAndLocalidadePaisIsNotNull(Long id);

    @Modifying
    @Query(value = "DELETE FROM candidato_vaga_interesse " +
                   "WHERE oportunidade_id = :idVaga",  nativeQuery = true)
    void removerTodosInteressesDaVaga(@Param("idVaga") Long idVaga);
}
