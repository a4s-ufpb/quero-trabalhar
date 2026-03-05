package com.QueroTrabalhar.repository;

import com.QueroTrabalhar.domain.entity.PerfilRecrutador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerfilRecrutadorRepository extends JpaRepository<PerfilRecrutador, Long> {
}