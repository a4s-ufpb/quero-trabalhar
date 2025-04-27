package com.QeuroTrabalhar.Repository;

import com.QeuroTrabalhar.Entity.ExperienciaProfissional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExperienciaProfissionalRepository extends JpaRepository<ExperienciaProfissional, Long> {
}
