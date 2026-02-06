package com.QueroTrabalhar.repository;

import com.QueroTrabalhar.domain.entity.InteresseEmOportunidades;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InteresseEmOportunidadesRepository extends JpaRepository<InteresseEmOportunidades, Long> {
}
