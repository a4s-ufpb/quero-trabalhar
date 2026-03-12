package com.QueroTrabalhar.services;

import com.QueroTrabalhar.domain.dtos.tipoDeEmprego.TipoDeEmpregoRequestDTO;
import com.QueroTrabalhar.domain.dtos.tipoDeEmprego.TipoDeEmpregoResponseDTO;
import com.QueroTrabalhar.domain.entity.TipoDeEmprego;
import com.QueroTrabalhar.repository.TipoDeEmpregoRepository;
import com.QueroTrabalhar.services.exceptions.DataIntegrityViolationException;
import com.QueroTrabalhar.services.exceptions.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TipoDeEmpregoService {

    @Autowired
    private TipoDeEmpregoRepository tipoDeEmpregoRepository;

    public List<TipoDeEmpregoResponseDTO> listarAprovados() {
        return tipoDeEmpregoRepository.findByAprovadoTrue().stream()
                .map(TipoDeEmpregoResponseDTO::daEntidade)
                .collect(Collectors.toList());
    }

    public List<TipoDeEmpregoResponseDTO> listarNaoAprovados(){
        return tipoDeEmpregoRepository.findByAprovadoFalse().stream()
                .map(TipoDeEmpregoResponseDTO::daEntidade)
                .collect(Collectors.toList());
    }

    public TipoDeEmpregoResponseDTO buscarPorId(Long id) {
        return TipoDeEmpregoResponseDTO.daEntidade(tipoDeEmpregoRepository
                .findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Tipo de emprego não encontrado, id: "+ id)
                ));
    }

    public TipoDeEmpregoResponseDTO criarNoCatalogo(TipoDeEmpregoRequestDTO tipoDeEmprego) {
        if(tipoDeEmpregoRepository.existsByTitulo(tipoDeEmprego.titulo()))
            throw new DataIntegrityViolationException("Já existe um tipo de emprego com o título: "+ tipoDeEmprego.titulo());

        TipoDeEmprego tipoDeEmpregoCriar = TipoDeEmprego
                .criarTipoDeEmpregoAdmin(
                        tipoDeEmprego.titulo()
                        ,tipoDeEmprego.descricao());

        tipoDeEmpregoRepository.save(tipoDeEmpregoCriar);

        return TipoDeEmpregoResponseDTO.daEntidade(tipoDeEmpregoCriar);
    }

    public TipoDeEmpregoResponseDTO sugerirNoCatalogo(TipoDeEmpregoRequestDTO tipoDeEmprego) {
        if(tipoDeEmpregoRepository.existsByTitulo(tipoDeEmprego.titulo()))
            throw new DataIntegrityViolationException("Já existe um tipo de emprego com o título: "+ tipoDeEmprego.titulo());

        TipoDeEmprego tipoDeEmpregoSugerir = TipoDeEmprego
                .criarTipoDeEmpregoSugeridoPeloUsuario(
                        tipoDeEmprego.titulo()
                        ,tipoDeEmprego.descricao());

        tipoDeEmpregoRepository.save(tipoDeEmpregoSugerir);

        return TipoDeEmpregoResponseDTO.daEntidade(tipoDeEmpregoSugerir);
    }

    public TipoDeEmpregoResponseDTO aprovarSugestao(Long id, String tituloCorrigido, String descricao) {
        TipoDeEmprego pendenteAprovar = tipoDeEmpregoRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Tipo de emprego não encontrado, id: " + id));

        // O Admin pode corrigir erros de português antes de aprovar
        pendenteAprovar.setTitulo(tituloCorrigido);
        pendenteAprovar.setDescricao(descricao);
        pendenteAprovar.setAprovado(true);

        tipoDeEmpregoRepository.save(pendenteAprovar);

        return TipoDeEmpregoResponseDTO.daEntidade(tipoDeEmpregoRepository.save(pendenteAprovar));
    }

    @Transactional
    public void aprovarEmLote(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new DataIntegrityViolationException("A lista de IDs não pode estar vazia.");
        }

        int registrosAtualizados = tipoDeEmpregoRepository.aprovarEmLote(ids);

        if (registrosAtualizados == 0) {
            throw new DataIntegrityViolationException("Nenhum tipo de emprego encontrado para os IDs informados.");
        }
    }

    public void deletar(Long id) {
        if(!tipoDeEmpregoRepository.existsById(id))
            throw new ObjectNotFoundException("Não existe um tipo de emprego com o id: "+id);
        tipoDeEmpregoRepository.deleteById(id);
    }
}