package com.QueroTrabalhar.repository;

import com.QueroTrabalhar.domain.entity.localidade.LocalidadePendente;
import com.QueroTrabalhar.domain.enums.StatusValidacaoLocalidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocalidadePendenteRepository extends JpaRepository<LocalidadePendente, Long> {

    List<LocalidadePendente> findByStatusValidacao(StatusValidacaoLocalidade statusValidacao);
}
