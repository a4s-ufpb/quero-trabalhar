package com.QueroTrabalhar.services.localidade;

import com.QueroTrabalhar.domain.entity.localidade.Cidade;
import com.QueroTrabalhar.domain.entity.localidade.Estado;
import com.QueroTrabalhar.domain.entity.localidade.Localidade;
import com.QueroTrabalhar.domain.entity.localidade.Pais;
import com.QueroTrabalhar.repository.CidadeRepository;
import com.QueroTrabalhar.repository.EstadoRepository;
import com.QueroTrabalhar.repository.PaisRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ResolvedorLocalidadeBaseInterna {

    private static final Logger logger = LoggerFactory.getLogger(ResolvedorLocalidadeBaseInterna.class);

    private final CidadeRepository cidadeRepository;
    private final EstadoRepository estadoRepository;
    private final PaisRepository paisRepository;

    public ResolvedorLocalidadeBaseInterna(
            CidadeRepository cidadeRepository,
            EstadoRepository estadoRepository,
            PaisRepository paisRepository
    ) {
        this.cidadeRepository = cidadeRepository;
        this.estadoRepository = estadoRepository;
        this.paisRepository = paisRepository;
    }

    public ResultadoResolucaoBaseInterna resolver(String textoNormalizado, String textoHash) {
        List<Cidade> cidades = cidadeRepository.findAllByNomeIgnoreCase(textoNormalizado);
        List<Estado> estados = estadoRepository.findAllByNomeIgnoreCase(textoNormalizado);
        Optional<Pais> paisOptional = paisRepository.findFirstByNomeIgnoreCase(textoNormalizado);

        int quantidadePossibilidades = cidades.size() + estados.size() + (paisOptional.isPresent() ? 1 : 0);
        if (quantidadePossibilidades > 1) {
            // A base interna nao deve escolher automaticamente entre resultados compativeis.
            logger.warn(
                    "event=localidade_base_interna_ambigua textoHash={} quantidadeCidades={} quantidadeEstados={} quantidadePaises={} quantidadePossibilidades={} acao=PENDENCIA",
                    textoHash,
                    cidades.size(),
                    estados.size(),
                    paisOptional.isPresent() ? 1 : 0,
                    quantidadePossibilidades
            );
            return ResultadoResolucaoBaseInterna.ambigua();
        }

        if (cidades.size() == 1) {
            Cidade cidade = cidades.getFirst();
            Estado estado = cidade.getEstado();
            return ResultadoResolucaoBaseInterna.resolvida(new Localidade(estado.getPais(), estado, cidade));
        }
        if (estados.size() == 1) {
            Estado estado = estados.getFirst();
            return ResultadoResolucaoBaseInterna.resolvida(new Localidade(estado.getPais(), estado));
        }
        if (paisOptional.isPresent()) {
            return ResultadoResolucaoBaseInterna.resolvida(new Localidade(paisOptional.get()));
        }

        logger.info("event=localidade_base_interna_sem_match textoHash={}", textoHash);
        return ResultadoResolucaoBaseInterna.semMatch();
    }
}
