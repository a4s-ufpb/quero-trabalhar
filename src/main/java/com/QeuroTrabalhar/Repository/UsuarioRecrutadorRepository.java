package com.QeuroTrabalhar.Repository;

import com.QeuroTrabalhar.Entity.UsuarioRecrutador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRecrutadorRepository extends JpaRepository<UsuarioRecrutador, Long> {
}
