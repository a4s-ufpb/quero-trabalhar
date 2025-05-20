package com.QueroTrabalhar.Repository;

import com.QueroTrabalhar.Entity.UsuarioRecrutador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRecrutadorRepository extends JpaRepository<UsuarioRecrutador, Long> {
}
