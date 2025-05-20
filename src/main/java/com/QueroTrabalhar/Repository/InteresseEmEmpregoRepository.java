package com.QueroTrabalhar.Repository;

import com.QueroTrabalhar.Entity.InteresseEmEmprego;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InteresseEmEmpregoRepository extends JpaRepository<InteresseEmEmprego, Long> {
}
