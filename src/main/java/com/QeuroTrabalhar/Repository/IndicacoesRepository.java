package com.QeuroTrabalhar.Repository;

import com.QeuroTrabalhar.Entity.Indicacoes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IndicacoesRepository extends JpaRepository<Indicacoes, Long> {
}
