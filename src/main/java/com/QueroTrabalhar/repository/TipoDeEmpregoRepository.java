package com.QueroTrabalhar.repository;

import com.QueroTrabalhar.domain.entity.TipoDeEmprego;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TipoDeEmpregoRepository extends JpaRepository<TipoDeEmprego, Long> {
    Optional<TipoDeEmprego> findByTitulo(String titulo); // Busca por Titulo

    List<TipoDeEmprego> findByAprovadoTrue();

    List<TipoDeEmprego> findByAprovadoFalse();

    Optional<TipoDeEmprego> findByTituloIgnoreCase(String titulo);
}
