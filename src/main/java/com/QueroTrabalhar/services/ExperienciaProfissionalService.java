package com.QueroTrabalhar.services;

import com.QueroTrabalhar.domain.dtos.experienciaProfissional.ExperienciaProfissionalRequestDTO;
import com.QueroTrabalhar.domain.dtos.experienciaProfissional.ExperienciaProfissionalResponseDTO;
import com.QueroTrabalhar.domain.entity.ExperienciaProfissional;
import com.QueroTrabalhar.domain.entity.PerfilCandidato;
import com.QueroTrabalhar.domain.entity.TipoDeEmprego;
import com.QueroTrabalhar.domain.entity.Usuario;
import com.QueroTrabalhar.repository.ExperienciaProfissionalRepository;
import com.QueroTrabalhar.repository.TipoDeEmpregoRepository;
import com.QueroTrabalhar.repository.UsuarioRepository;
import com.QueroTrabalhar.services.exceptions.DataIntegrityViolationException;
import com.QueroTrabalhar.services.exceptions.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExperienciaProfissionalService {

    @Autowired
    private ExperienciaProfissionalRepository experienciaProfissionalRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TipoDeEmpregoRepository tipoDeEmpregoRepository;

    private PerfilCandidato obterMeuPerfilCandidato(){
        String emailLogado = SecurityContextHolder.getContext().getAuthentication().getName();

        Usuario usuario = usuarioRepository.findByEmail(emailLogado)
                .orElseThrow(() -> new ObjectNotFoundException("Usuário não encontrado no contexto de segurança."));

        if(!usuario.ehCandidato()){
            throw new AccessDeniedException("Acesso negado. Você precisa ter um perfil de candidato ativo para gerenciar experiências.");
        }

        return usuario.getPerfilCandidato();
    }

    @Transactional(readOnly = true)
    public List<ExperienciaProfissionalResponseDTO> listarTodasAsExperienciaProfissionais() {
        PerfilCandidato candidato = obterMeuPerfilCandidato();

        return candidato.getExperiencias().stream()
                .map(ExperienciaProfissionalResponseDTO::daEntidade)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ExperienciaProfissionalResponseDTO buscarMinhaExperienciaPorId(Long idExperiencia) {
        PerfilCandidato perfil = obterMeuPerfilCandidato();

        ExperienciaProfissional exp = experienciaProfissionalRepository.findById(idExperiencia)
                .orElseThrow(() -> new ObjectNotFoundException("Experiência não encontrada. ID: " + idExperiencia));

        if (!exp.getPerfilCandidato().getId().equals(perfil.getId())) {
            throw new AccessDeniedException("Você não tem permissão para acessar esta experiência.");
        }

        return ExperienciaProfissionalResponseDTO.daEntidade(exp);
    }

    @Transactional
    public ExperienciaProfissionalResponseDTO adicionarMinhaExperienciaProfissional(ExperienciaProfissionalRequestDTO dto) {
        PerfilCandidato perfil = obterMeuPerfilCandidato();

        TipoDeEmprego tipo = tipoDeEmpregoRepository.findById(dto.tipoDeEmpregoId())
                .orElseThrow(() -> new ObjectNotFoundException("Tipo de Emprego não encontrado, ID: "+dto.tipoDeEmpregoId()));

        ExperienciaProfissional novaExperiencia = new ExperienciaProfissional(
                perfil,
                tipo,
                dto.descricao(),
                dto.dataInicio(),
                dto.dataFim()
        );

        perfil.adicionarExperiencia(novaExperiencia);
        return ExperienciaProfissionalResponseDTO.daEntidade(experienciaProfissionalRepository.save(novaExperiencia));
    }

    @Transactional
    public void deletarMinhaExperiencia(Long experienciaId) {
        PerfilCandidato perfil = obterMeuPerfilCandidato();

        ExperienciaProfissional exp = experienciaProfissionalRepository.findById(experienciaId)
                .orElseThrow(() -> new ObjectNotFoundException("Experiência não encontrada. ID: " + experienciaId));

        if(!exp.getPerfilCandidato().getId().equals(perfil.getId())){
            throw new DataIntegrityViolationException("Operação negada: Esta experiência não pertence ao perfil informado");
        }

        perfil.removerExperiencia(exp);

        experienciaProfissionalRepository.delete(exp);
    }

    public List<ExperienciaProfissionalResponseDTO> listarTodasExperienciasComoAdmin() {
        return experienciaProfissionalRepository.findAll().stream()
                .map(ExperienciaProfissionalResponseDTO::daEntidade)
                .collect(Collectors.toList());
    }

    public ExperienciaProfissionalResponseDTO buscarExperienciaPorIdComoAdmin(Long id) {
        ExperienciaProfissional exp = experienciaProfissionalRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Experiência não encontrada. ID: " + id));
        return ExperienciaProfissionalResponseDTO.daEntidade(exp);
    }

    @Transactional
    public ExperienciaProfissionalResponseDTO atualizarExperienciaComoAdmin(Long id, ExperienciaProfissionalRequestDTO dto) {
        ExperienciaProfissional exp = experienciaProfissionalRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Experiência não encontrada. ID: " + id));

        TipoDeEmprego tipo = tipoDeEmpregoRepository.findById(dto.tipoDeEmpregoId())
                .orElseThrow(() -> new ObjectNotFoundException("Tipo de emprego não encontrado. ID: "+dto.tipoDeEmpregoId()));

        exp.setTipoDeEmprego(tipo);
        exp.setDescricao(dto.descricao());
        exp.setDataInicio(dto.dataInicio());
        exp.setDataFim(dto.dataFim());

        return ExperienciaProfissionalResponseDTO.daEntidade(experienciaProfissionalRepository.save(exp));
    }

    @Transactional
    public void deletarExperienciaComoAdmin(Long idExperiencia) {
        ExperienciaProfissional exp = experienciaProfissionalRepository.findById(idExperiencia)
                .orElseThrow(() -> new ObjectNotFoundException("Experiência não encontrada. ID: " + idExperiencia));

        if (exp.getPerfilCandidato() != null) {
            exp.getPerfilCandidato().removerExperiencia(exp);
        }

        experienciaProfissionalRepository.delete(exp);
    }
}
