package com.QeuroTrabalhar.Services;

import com.QeuroTrabalhar.Entity.ExperienciaProfissional;
import com.QeuroTrabalhar.Repository.ExperienciaProfissionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service // Marca esta classe como um serviço do Spring
public class ExperienciaProfissionalService {

    @Autowired // Injeta as instâncias
    private ExperienciaProfissionalRepository experienciaProfissionalRepository; // Dependência do repositório

    // Lista todas as experiências profissionais
    public List<ExperienciaProfissional> listarTodasAsExperienciaProfissionais() {
        return experienciaProfissionalRepository.findAll();
    }

    // Lista as Experiência por ID
    public Optional<ExperienciaProfissional> listarPorId(Long id) {
        return experienciaProfissionalRepository.findById(id);
    }

    // Metodo para cadastrar uma nova experiência
    public ExperienciaProfissional cadastrarExperienciaProfissional(ExperienciaProfissional experienciaProfissional) {
        return experienciaProfissionalRepository.save(experienciaProfissional);
    }

    // Deletar Experiência Profissional
    public void deletarExperienciaProfissional(Long id) {
        experienciaProfissionalRepository.deleteById(id);
    }
}
