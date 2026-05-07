package com.QueroTrabalhar.services;

import com.QueroTrabalhar.domain.dtos.oportunidadeDeEmprego.OportunidadeDeEmpregoResponseDTO;
import com.QueroTrabalhar.domain.entity.OportunidadeDeEmprego;
import com.QueroTrabalhar.domain.entity.PerfilCandidato;
import com.QueroTrabalhar.domain.entity.localidade.LocalidadePendente;
import com.QueroTrabalhar.domain.enums.CampoLocalidadePendente;
import com.QueroTrabalhar.domain.enums.TipoRecursoLocalidadePendente;
import com.QueroTrabalhar.repository.LocalidadePendenteRepository;
import com.QueroTrabalhar.repository.OportunidadeDeEmpregoRepository;
import com.QueroTrabalhar.repository.PerfilCandidatoRepository;
import com.QueroTrabalhar.services.exceptions.BusinessRuleException;
import com.QueroTrabalhar.services.exceptions.ObjectNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PerfilCandidatoService {

    private final PerfilCandidatoRepository perfilCandidatoRepository;
    private final OportunidadeDeEmpregoRepository oportunidadeDeEmpregoRepository;
    private final LocalidadePendenteRepository localidadePendenteRepository;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public PerfilCandidatoService(
            PerfilCandidatoRepository perfilCandidatoRepository,
            OportunidadeDeEmpregoRepository oportunidadeDeEmpregoRepository,
            LocalidadePendenteRepository localidadePendenteRepository,
            UsuarioAutenticadoService usuarioAutenticadoService
    ) {
        this.perfilCandidatoRepository = perfilCandidatoRepository;
        this.oportunidadeDeEmpregoRepository = oportunidadeDeEmpregoRepository;
        this.localidadePendenteRepository = localidadePendenteRepository;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    @Transactional
    public void demonstrarInteresseEmVaga(Long vagaId) {
        PerfilCandidato perfilCandidato = usuarioAutenticadoService.obterPerfilCandidatoAutenticado();
        OportunidadeDeEmprego vaga = buscarVagaPorId(vagaId);

        if (perfilCandidato.getVagasDeInteresse().contains(vaga)) {
            throw new BusinessRuleException("Voce ja demonstrou interesse nesta vaga.");
        }

        perfilCandidato.demonstrarInteresse(vaga);
        perfilCandidatoRepository.save(perfilCandidato);
    }

    @Transactional
    public void removerInteresseEmVaga(Long vagaId) {
        PerfilCandidato perfilCandidato = usuarioAutenticadoService.obterPerfilCandidatoAutenticado();
        OportunidadeDeEmprego vaga = buscarVagaPorId(vagaId);

        if (!perfilCandidato.getVagasDeInteresse().contains(vaga)) {
            throw new BusinessRuleException("Voce ainda nao demonstrou interesse nesta vaga.");
        }

        perfilCandidato.removerInteresse(vaga);
        perfilCandidatoRepository.save(perfilCandidato);
    }

    @Transactional(readOnly = true)
    public List<OportunidadeDeEmpregoResponseDTO> listarMinhasVagasDeInteresse() {
        PerfilCandidato perfilCandidato = usuarioAutenticadoService.obterPerfilCandidatoAutenticado();
        List<OportunidadeDeEmprego> vagasDeInteresse = perfilCandidato.getVagasDeInteresse().stream().toList();
        Map<Long, LocalidadePendente> pendenciasPorOportunidade = mapearPendenciasPorOportunidade(vagasDeInteresse);

        return vagasDeInteresse.stream()
                .map(vaga -> OportunidadeDeEmpregoResponseDTO.daEntidade(
                        vaga,
                        pendenciasPorOportunidade.get(vaga.getId())
                ))
                .toList();
    }

    private OportunidadeDeEmprego buscarVagaPorId(Long vagaId) {
        return oportunidadeDeEmpregoRepository.findById(vagaId)
                .orElseThrow(() -> new ObjectNotFoundException(
                        "Oportunidade de emprego nao encontrada. ID: " + vagaId
                ));
    }

    private Map<Long, LocalidadePendente> mapearPendenciasPorOportunidade(List<OportunidadeDeEmprego> oportunidades) {
        List<Long> oportunidadeIds = oportunidades.stream()
                .filter(oportunidade -> oportunidade.getLocalizacao() == null)
                .map(OportunidadeDeEmprego::getId)
                .filter(id -> id != null)
                .distinct()
                .toList();

        if (oportunidadeIds.isEmpty()) {
            return Map.of();
        }

        Map<Long, LocalidadePendente> pendenciasPorOportunidade = new HashMap<>();
        localidadePendenteRepository.findByTipoRecursoAndCampoAlvoAndRecursoIdInOrderByRecursoIdAscAtualizadaEmDescCriadaEmDesc(
                TipoRecursoLocalidadePendente.OPORTUNIDADE_DE_EMPREGO,
                CampoLocalidadePendente.LOCALIDADE,
                oportunidadeIds
        ).forEach(pendencia -> pendenciasPorOportunidade.putIfAbsent(
                pendencia.getRecursoId(),
                pendencia
        ));

        return pendenciasPorOportunidade;
    }
}
