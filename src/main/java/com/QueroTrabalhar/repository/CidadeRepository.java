package com.QueroTrabalhar.repository;

import com.QueroTrabalhar.domain.entity.Cidade;
import com.QueroTrabalhar.domain.enums.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CidadeRepository extends JpaRepository<Cidade, Long> {
    List<Cidade> findByEstadoOrderByNome(Estado estado);
}
