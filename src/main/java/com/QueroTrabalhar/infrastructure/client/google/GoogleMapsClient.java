package com.QueroTrabalhar.infrastructure.client.google;

import com.QueroTrabalhar.infrastructure.client.google.dto.GoogleGeocodeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


import java.util.Optional;

@Component
public class GoogleMapsClient {

    private final RestTemplate restTemplate;

    @Value("${google.maps.api.url}")
    private String domain;

    @Value("${google.maps.api.path}")
    private String path;

    @Value("${google.maps.api.key}")
    private String apiKey;


    public GoogleMapsClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Busca um lugar no Google Geocoding API com suporte a filtros de componentes.
     *
     * @param termoBusca O texto digitado pelo usuário (Ex: "bra", "caj").
     * @param filtros    O filtro estruturado do Google (Ex: "country:BR|administrative_area:PB"). Pode ser null.
     */
    public Optional<GoogleGeocodeResponse> buscarLugarComFiltro(String termoBusca, String filtros) {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(domain + path)
                    .queryParam("address", termoBusca)
                    .queryParam("language", "pt-BR")
                    .queryParam("key", apiKey);

            // Injeta o filtro de componentes apenas se ele foi exigido pelo Service (estados e cidades são obrigatórias)
            if (filtros != null && !filtros.isBlank()) {
                builder.queryParam("components", filtros);
            }

            // O Spring RestTemplate resolve a URL final e faz o encode seguro automaticamente a partir da URI e retorna da reposta do JSON no formato GoogleGeocodeResponse
            GoogleGeocodeResponse response = restTemplate.getForObject(builder.build().toUri(), GoogleGeocodeResponse.class);
            return Optional.ofNullable(response);

        } catch (Exception e) {
            //Todo: talvez melhorar esse tratamento de erro aqui, colocar um especifico para o Google.
            System.err.println("Erro ao comunicar com Google Maps API: " + e.getMessage());
            return Optional.empty();
        }
    }
}