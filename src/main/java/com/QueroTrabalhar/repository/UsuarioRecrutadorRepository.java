package com.QueroTrabalhar.repository;

import com.QueroTrabalhar.domain.entity.UsuarioRecrutador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRecrutadorRepository extends JpaRepository<UsuarioRecrutador, Long> {
}
