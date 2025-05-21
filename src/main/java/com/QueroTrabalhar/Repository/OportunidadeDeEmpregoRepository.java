package com.QueroTrabalhar.Repository;

import com.QueroTrabalhar.Entity.OportunidadeDeEmprego;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OportunidadeDeEmpregoRepository extends JpaRepository<OportunidadeDeEmprego, Long> {
}
