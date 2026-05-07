package com.QueroTrabalhar.repository;

import com.QueroTrabalhar.domain.entity.Empresa;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long>, JpaSpecificationExecutor<Empresa> {

    List<Empresa> findAllByOrderByNomeAsc();

    List<Empresa> findByLocalidadePaisIsNotNullOrderByNomeAsc();

    Optional<Empresa> findByIdAndLocalidadePaisIsNotNull(Long id);
}
