package com.QueroTrabalhar.repository;

import com.QueroTrabalhar.domain.entity.OportunidadeDeEmprego;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OportunidadeDeEmpregoRepository extends JpaRepository<OportunidadeDeEmprego, Long> {
}
