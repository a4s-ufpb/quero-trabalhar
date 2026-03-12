package com.QueroTrabalhar.services;

import com.QueroTrabalhar.domain.dtos.CidadeIbgeDTO;
import com.QueroTrabalhar.domain.entity.Cidade;
import com.QueroTrabalhar.domain.enums.Estado;
import com.QueroTrabalhar.repository.CidadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class LocalidadeSyncService {

    @Autowired
    private CidadeRepository repository;

    private final RestTemplate restTemplate = new RestTemplate();

    @Transactional
    public void sincronizarEstado(Integer codigoUf) {
        String url = "https://servicodados.ibge.gov.br/api/v1/localidades/estados/" + codigoUf + "/municipios";
        CidadeIbgeDTO[] dados = restTemplate.getForObject(url, CidadeIbgeDTO[].class);

        if (dados != null) {
            List<Cidade> Cidades = Arrays.stream(dados)
                    .map(dto -> new Cidade(dto.id(), dto.nome(), Estado.valueOf(dto.getSiglaEstado())))
                    .toList();
            repository.saveAll(Cidades); // Realiza o "Upsert"
        }
    }
}
