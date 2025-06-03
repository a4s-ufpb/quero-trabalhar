package com.QueroTrabalhar.repository;

import com.QueroTrabalhar.entity.InteresseEmEmprego;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InteresseEmEmpregoRepository extends JpaRepository<InteresseEmEmprego, Long> {
}
