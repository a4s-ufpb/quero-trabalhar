package com.QueroTrabalhar.repository;

import com.QueroTrabalhar.domain.entity.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    List<Empresa> findAllByOrderByNomeAsc();

    List<Empresa> findByLocalidadePaisIsNotNullOrderByNomeAsc();
}
