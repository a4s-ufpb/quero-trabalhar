package com.QueroTrabalhar.repository;


import com.QueroTrabalhar.domain.entity.localidade.Pais;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaisRepository extends JpaRepository<Pais, Long> {

    // SÓ ISSO! Apague o @Query. O Spring faz o resto.
    // Isso gera o SQL: SELECT * FROM pais WHERE UPPER(nome) LIKE UPPER('%termoBusca%')
    List<Pais> findByNomeContainingIgnoreCase(String termoBusca);

    // Bônus: Aquele método que usamos no Service para o upsert
    Optional<Pais> findBySigla(String sigla);
}
