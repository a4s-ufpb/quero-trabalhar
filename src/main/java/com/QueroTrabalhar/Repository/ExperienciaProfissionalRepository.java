package com.QueroTrabalhar.Repository;

import com.QueroTrabalhar.Entity.ExperienciaProfissional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExperienciaProfissionalRepository extends JpaRepository<ExperienciaProfissional, Long> {
}
