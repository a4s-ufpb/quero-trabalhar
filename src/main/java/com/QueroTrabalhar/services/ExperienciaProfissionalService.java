package com.QueroTrabalhar.services;

import com.QueroTrabalhar.domain.dtos.experienciaProfissional.ExperienciaProfissionalRequestDTO;
import com.QueroTrabalhar.domain.dtos.experienciaProfissional.ExperienciaProfissionalResponseDTO;
import com.QueroTrabalhar.domain.entity.ExperienciaProfissional;
import com.QueroTrabalhar.domain.entity.PerfilCandidato;
import com.QueroTrabalhar.domain.entity.TipoDeEmprego;
import com.QueroTrabalhar.repository.ExperienciaProfissionalRepository;
import com.QueroTrabalhar.repository.PerfilCandidatoRepository;
import com.QueroTrabalhar.repository.TipoDeEmpregoRepository;
import com.QueroTrabalhar.services.exceptions.DataIntegrityViolationException;
import com.QueroTrabalhar.services.exceptions.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExperienciaProfissionalService {

    @Autowired
    private ExperienciaProfissionalRepository experienciaProfissionalRepository;

    @Autowired
    private PerfilCandidatoRepository perfilCandidatoRepository;

    @Autowired
    private TipoDeEmpregoRepository tipoDeEmpregoRepository;

    public List<ExperienciaProfissionalResponseDTO> listarTodasAsExperienciaProfissionais() {
        return experienciaProfissionalRepository.findAll().stream()
                .map(ExperienciaProfissionalResponseDTO::daEntidade)
                .collect(Collectors.toList());
    }

    public ExperienciaProfissionalResponseDTO buscarPorId(Long id) {
        return ExperienciaProfissionalResponseDTO.daEntidade(encontrarExperienciaProfissional(id));
    }



    public ExperienciaProfissionalResponseDTO criarExperienciaProfissional(Long id, ExperienciaProfissionalRequestDTO dto) {
        PerfilCandidato perfil = perfilCandidatoRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Perfil não encontrado"));

        TipoDeEmprego tipo = tipoDeEmpregoRepository.findById(dto.tipoDeEmpregoId())
                .orElseThrow(() -> new ObjectNotFoundException("Tipo de emprego inválido"));


        ExperienciaProfissional exp = new ExperienciaProfissional(
                perfil,
                tipo,
                dto.descricao(),
                dto.dataInicio(),
                dto.dataFim()
        );

        return ExperienciaProfissionalResponseDTO.daEntidade(experienciaProfissionalRepository.save(exp));
    }

    public void deletarExperiencia(Long perfilCandidatoId, Long experienciaId) {
        ExperienciaProfissional exp = encontrarExperienciaProfissional(experienciaId);

        if(!exp.getPerfilCandidato().getId().equals(perfilCandidatoId)){
            throw new DataIntegrityViolationException("Operação negada: Esta experiência não pertence ao perfil informado");
        }

        experienciaProfissionalRepository.delete(exp);
    }

    private ExperienciaProfissional encontrarExperienciaProfissional(Long id) {
        return experienciaProfissionalRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Experiência profissional não encontrada, ID: " + id));
    }
}
