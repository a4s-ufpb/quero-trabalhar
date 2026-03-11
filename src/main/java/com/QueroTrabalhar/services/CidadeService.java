package com.QueroTrabalhar.services;

import com.QueroTrabalhar.domain.dtos.CidadeIbgeDTO;
import com.QueroTrabalhar.domain.entity.Cidade;
import com.QueroTrabalhar.domain.enums.Estado;
import com.QueroTrabalhar.repository.CidadeRepository;
import org.springframework.cache.annotation.Cacheable; // AJUSTADO: Import correto do Spring
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional; // AJUSTADO: Use o do Spring
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class CidadeService {

    @Autowired
    private CidadeRepository repository;

    private final RestTemplate restTemplate = new RestTemplate();

    @Cacheable(value = "cidadesPorEstado", key = "#a0")
    public List<Cidade> buscarPorEstado(Estado estado) {
        return repository.findByEstadoOrderByNome(estado);
    }

    /**
     * AJUSTADO: JpaRepository.findById retorna Optional.
     * Fizemos o tratamento direto para evitar o erro de tipos.
     */
    public Cidade findById(Long idIbge) {
        return repository.findById(idIbge)
                .orElseThrow(() -> new RuntimeException("Cidade não encontrada com o ID: " + idIbge));
    }

    @Transactional
    @CacheEvict(value = "cidadesPorEstado", allEntries = true)
    public void sincronizarEstado(Integer codigoUf) {
        String url = "https://servicodados.ibge.gov.br/api/v1/localidades/estados/" + codigoUf + "/municipios";

        CidadeIbgeDTO[] dadosApi = restTemplate.getForObject(url, CidadeIbgeDTO[].class);

        if (dadosApi != null) {
            List<Cidade> cidadesParaSalvar = Arrays.stream(dadosApi)
                    .map(dto -> new Cidade(
                            dto.id().longValue(),
                            dto.nome(),
                            Estado.valueOf(dto.getSiglaEstado()) // AJUSTADO: Usando o helper do DTO
                    ))
                    .toList();

            repository.saveAll(cidadesParaSalvar);
        }
    }

    public void sincronizarBrasilTodo() {
        int[] idsEstados = {11, 12, 13, 14, 15, 16, 17, 21, 22, 23, 24, 25, 26, 27, 28, 29, 31, 32, 33, 35, 41, 42, 43, 50, 51, 52, 53};
        for (int id : idsEstados) {
            this.sincronizarEstado(id);
        }
    }

    /**
     * MÉTODO TEMPORÁRIO PARA GERAR O DATA.SQL
     * Rode isso apenas uma vez e copie o resultado do console.
     */
    public void gerarScriptSqlDeTodasAsCidades() {
        System.out.println("--- INICIANDO GERAÇÃO DO SQL ---");
        int[] idsEstados = {11, 12, 13, 14, 15, 16, 17, 21, 22, 23, 24, 25, 26, 27, 28, 29, 31, 32, 33, 35, 41, 42, 43, 50, 51, 52, 53};

        for (int uf : idsEstados) {
            String url = "https://servicodados.ibge.gov.br/api/v1/localidades/estados/" + uf + "/municipios";
            CidadeIbgeDTO[] dadosApi = restTemplate.getForObject(url, CidadeIbgeDTO[].class);

            if (dadosApi != null) {
                for (CidadeIbgeDTO dto : dadosApi) {
                    // O replace previne erros com cidades que têm aspas no nome, ex: Pau d'Arco
                    String nomeTratado = dto.nome().replace("'", "''");
                    String sql = String.format(
                            "INSERT INTO cidades (id_ibge, nome, estado) VALUES (%d, '%s', '%s');",
                            dto.id(), nomeTratado, dto.getSiglaEstado()
                    );
                    System.out.println(sql);
                }
            }
        }
        System.out.println("--- FIM DA GERAÇÃO ---");
    }
}