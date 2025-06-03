package com.QueroTrabalhar.repository;

import com.QueroTrabalhar.entity.TipoDeEmprego;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoDeEmpregoRepository extends JpaRepository<TipoDeEmprego, Long> {
    Optional<TipoDeEmprego> findByTitulo(String titulo); // Busca por Titulo
}
