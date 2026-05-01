package com.QueroTrabalhar.services;

import com.QueroTrabalhar.domain.dtos.oportunidadeDeEmprego.OportunidadeDeEmpregoResponseDTO;
import com.QueroTrabalhar.domain.entity.OportunidadeDeEmprego;
import com.QueroTrabalhar.domain.entity.PerfilCandidato;
import com.QueroTrabalhar.repository.OportunidadeDeEmpregoRepository;
import com.QueroTrabalhar.repository.PerfilCandidatoRepository;
import com.QueroTrabalhar.services.exceptions.BusinessRuleException;
import com.QueroTrabalhar.services.exceptions.ObjectNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PerfilCandidatoService {

    private final PerfilCandidatoRepository perfilCandidatoRepository;
    private final OportunidadeDeEmpregoRepository oportunidadeDeEmpregoRepository;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public PerfilCandidatoService(
            PerfilCandidatoRepository perfilCandidatoRepository,
            OportunidadeDeEmpregoRepository oportunidadeDeEmpregoRepository,
            UsuarioAutenticadoService usuarioAutenticadoService
    ) {
        this.perfilCandidatoRepository = perfilCandidatoRepository;
        this.oportunidadeDeEmpregoRepository = oportunidadeDeEmpregoRepository;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    @Transactional
    public void demonstrarInteresseEmVaga(Long vagaId) {
        PerfilCandidato perfilCandidato = usuarioAutenticadoService.obterPerfilCandidatoAutenticado();
        OportunidadeDeEmprego vaga = buscarVagaPorId(vagaId);

        if (perfilCandidato.getVagasDeInteresse().contains(vaga)) {
            throw new BusinessRuleException("Você já demonstrou interesse nesta vaga.");
        }

        perfilCandidato.demonstrarInteresse(vaga);
        perfilCandidatoRepository.save(perfilCandidato);
    }

    @Transactional
    public void removerInteresseEmVaga(Long vagaId) {
        PerfilCandidato perfilCandidato = usuarioAutenticadoService.obterPerfilCandidatoAutenticado();
        OportunidadeDeEmprego vaga = buscarVagaPorId(vagaId);

        if (!perfilCandidato.getVagasDeInteresse().contains(vaga)) {
            throw new BusinessRuleException("Você ainda não demonstrou interesse nesta vaga.");
        }

        perfilCandidato.removerInteresse(vaga);
        perfilCandidatoRepository.save(perfilCandidato);
    }

    @Transactional(readOnly = true)
    public List<OportunidadeDeEmpregoResponseDTO> listarMinhasVagasDeInteresse() {
        PerfilCandidato perfilCandidato = usuarioAutenticadoService.obterPerfilCandidatoAutenticado();

        return perfilCandidato.getVagasDeInteresse().stream()
                .map(OportunidadeDeEmpregoResponseDTO::daEntidade)
                .toList();
    }

    private OportunidadeDeEmprego buscarVagaPorId(Long vagaId) {
        return oportunidadeDeEmpregoRepository.findById(vagaId)
                .orElseThrow(() -> new ObjectNotFoundException(
                        "Oportunidade de emprego não encontrada. ID: " + vagaId
                ));
    }
}
