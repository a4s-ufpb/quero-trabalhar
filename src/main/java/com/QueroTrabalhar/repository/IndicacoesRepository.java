package com.QueroTrabalhar.repository;

import com.QueroTrabalhar.entity.Indicacoes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IndicacoesRepository extends JpaRepository<Indicacoes, Long> {
}
