package com.QueroTrabalhar.repository;

import com.QueroTrabalhar.domain.entity.Indicacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IndicacaoRepository extends JpaRepository<Indicacao, Long> {
}
