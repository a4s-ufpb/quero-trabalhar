package com.QueroTrabalhar.services;

import com.QueroTrabalhar.domain.entity.TipoDeEmprego;
import com.QueroTrabalhar.repository.TipoDeEmpregoRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TipoDeEmpregoService {

    @Autowired
    private TipoDeEmpregoRepository tipoDeEmpregoRepository;

    // --- MÉTODOS DE BUSCA ---

    // 1. Para o Dropdown do Front-end: Retorna APENAS os aprovados
    public List<TipoDeEmprego> listarTiposAprovados() {
        return tipoDeEmpregoRepository.findByAprovadoTrue();
    }

    // 2. Para o Painel do ADMIN: Retorna as sugestões que precisam de revisão
    public List<TipoDeEmprego> listarSugestoesPendentes() {
        return tipoDeEmpregoRepository.findByAprovadoFalse();
    }

    public Optional<TipoDeEmprego> buscarPorId(Long id) {
        return tipoDeEmpregoRepository.findById(id);
    }

    // --- MÉTODOS DE CRIAÇÃO E VALIDAÇÃO ---

    // 3. Usado pelo ADMIN para criar um cargo oficial no sistema
    public TipoDeEmprego salvarOficial(TipoDeEmprego tipoDeEmprego) {
        // Usamos IgnoreCase para evitar que criem "Dev" e "dev" como coisas diferentes
        Optional<TipoDeEmprego> existente = tipoDeEmpregoRepository.findByTituloIgnoreCase(tipoDeEmprego.getTitulo());

        if (existente.isPresent()) {
            // Resolvendo seu TODO: EntityExistsException é a exceção padrão do JPA para conflitos
            throw new EntityExistsException("Já existe um tipo de emprego com este título no sistema.");
        }

        tipoDeEmprego.setAprovado(true);
        return tipoDeEmpregoRepository.save(tipoDeEmprego);
    }

    // 4. A MÁGICA DO FLUXO DINÂMICO: Usado quando o usuário digita uma profissão nova
    public TipoDeEmprego obterOuCriarSugestao(String tituloSugerido) {
        // Se já existe (aprovado ou não), reaproveita para não duplicar a mesma sugestão
        return tipoDeEmpregoRepository.findByTituloIgnoreCase(tituloSugerido)
                .orElseGet(() -> {
                    // Se não existe na base, cria como sugestão pendente (aprovado = false)
                    TipoDeEmprego novaSugestao = new TipoDeEmprego(tituloSugerido);
                    return tipoDeEmpregoRepository.save(novaSugestao);
                });
    }

    // 5. Usado pelo ADMIN para validar a sugestão de um usuário
    public TipoDeEmprego aprovarSugestao(Long id, String tituloCorrigido, String descricao) {
        TipoDeEmprego pendente = tipoDeEmpregoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de emprego não encontrado."));

        // O Admin pode corrigir erros de português antes de aprovar
        pendente.setTitulo(tituloCorrigido);
        pendente.setDescricao(descricao);
        pendente.setAprovado(true);

        return tipoDeEmpregoRepository.save(pendente);
    }

    // --- MÉTODOS DE DELEÇÃO ---

    public void deletar(Long id) {
        tipoDeEmpregoRepository.deleteById(id);
    }
}