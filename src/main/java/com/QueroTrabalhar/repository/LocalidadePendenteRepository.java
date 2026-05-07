package com.QueroTrabalhar.repository;

import com.QueroTrabalhar.domain.entity.localidade.LocalidadePendente;
import com.QueroTrabalhar.domain.enums.CampoLocalidadePendente;
import com.QueroTrabalhar.domain.enums.StatusValidacaoLocalidade;
import com.QueroTrabalhar.domain.enums.TipoRecursoLocalidadePendente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocalidadePendenteRepository extends JpaRepository<LocalidadePendente, Long> {

    List<LocalidadePendente> findByStatusValidacao(StatusValidacaoLocalidade statusValidacao);

    Optional<LocalidadePendente> findFirstByTipoRecursoAndRecursoIdAndCampoAlvo(
            TipoRecursoLocalidadePendente tipoRecurso,
            Long recursoId,
            CampoLocalidadePendente campoAlvo
    );
}
