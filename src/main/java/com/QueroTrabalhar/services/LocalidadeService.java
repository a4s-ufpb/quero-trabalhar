package com.QueroTrabalhar.services;

import com.QueroTrabalhar.domain.dtos.localidade.CidadeResponseDTO;
import com.QueroTrabalhar.domain.dtos.localidade.EstadoResponseDTO;
import com.QueroTrabalhar.domain.dtos.localidade.PaisResponseDTO;
import com.QueroTrabalhar.domain.entity.localidade.Cidade;
import com.QueroTrabalhar.domain.entity.localidade.Estado;
import com.QueroTrabalhar.domain.entity.localidade.Pais;
import com.QueroTrabalhar.repository.CidadeRepository;
import com.QueroTrabalhar.repository.EstadoRepository;
import com.QueroTrabalhar.repository.PaisRepository;
import com.QueroTrabalhar.infrastructure.client.google.GoogleMapsClient;
import com.QueroTrabalhar.infrastructure.client.google.dto.AddressComponent;
import com.QueroTrabalhar.infrastructure.client.google.dto.GoogleGeocodeResponse;
import com.QueroTrabalhar.infrastructure.client.google.dto.GoogleResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class LocalidadeService {

    private final PaisRepository paisRepository;
    private final EstadoRepository estadoRepository;
    private final CidadeRepository cidadeRepository;
    private final GoogleMapsClient googleClient;

    public LocalidadeService(PaisRepository paisRepository,
                             EstadoRepository estadoRepository,
                             CidadeRepository cidadeRepository,
                             GoogleMapsClient googleClient) {
        this.paisRepository = paisRepository;
        this.estadoRepository = estadoRepository;
        this.cidadeRepository = cidadeRepository;
        this.googleClient = googleClient;
    }

    /**
     *
     * @param termoBusca
     * @return
     */
    @Transactional(readOnly = true)
    public List<PaisResponseDTO> buscarPais(String termoBusca){
        //195 paises já cadastrados no banco
        List<Pais> paisesLocais = paisRepository.findByNomeContainingIgnoreCase(termoBusca);
        return paisesLocais.stream().map(PaisResponseDTO::daEntidade).toList();
    }

    /**
     *
     * @param paisId
     * @param termoBusca
     * @return
     */
    @Transactional
    public List<EstadoResponseDTO> buscarEstado(Long paisId, String termoBusca) {
        //Todo: tratar exceção
        Pais pais = paisRepository.findById(paisId)
                .orElseThrow(() -> new IllegalArgumentException("País não encontrado."));

        List<Estado> estadosLocais = estadoRepository.findByPaisAndNomeContainingIgnoreCase(pais, termoBusca);
        if (!estadosLocais.isEmpty()) {
            return estadosLocais.stream().map(EstadoResponseDTO::daEntidade).toList();
        }

        //Fallback Google (Muro de contenção: Apenas dentro do País conhecido)
        String filtro = String.format("country:%s", pais.getSigla());
        Optional<GoogleGeocodeResponse> response = googleClient.buscarLugarComFiltro(termoBusca, filtro);

        //Todo: tratar exceção
        if (response.isEmpty() || !response.get().isSucesso()) return Collections.emptyList();

        // 3. Extração e Upsert
        GoogleResult googleResult = response.get().results().getFirst();
        AddressComponent componente = extrairComponente(googleResult, "administrative_area_level_1"); //administrative_area_level_1 referencia estados ou equivalente na API;
        //Todo: tratar exceção
        if (componente == null) return Collections.emptyList();

        //Verificação Upsert
        Estado estadoSalvo = estadoRepository.findByNomeAndPais(componente.longName(), pais)
                .orElseGet(() -> estadoRepository.save(new Estado(componente.longName(), componente.shortName(), pais)));

        //Vou deixar aqui para caso o front queira usar em algum momento
        //String formatedAddress = response.get().results().getFirst().formattedAddress();

        return List.of(EstadoResponseDTO.daEntidade(estadoSalvo));
    }

    /**
     *
     * @param estadoId
     * @param termoBusca
     * @return
     */
    @Transactional
    public List<CidadeResponseDTO> buscarCidade(Long estadoId, String termoBusca) {
        //Todo: tratar exceção
        Estado estado = estadoRepository.findById(estadoId)
                .orElseThrow(() -> new IllegalArgumentException("Estado não encontrado."));

        List<Cidade> cidadesLocais = cidadeRepository.findByEstadoAndNomeContainingIgnoreCase(estado, termoBusca);
        if (!cidadesLocais.isEmpty()) {
            return cidadesLocais.stream().map(CidadeResponseDTO::daEntidade).toList();
        }

        //Fallback Google (Muro Triplo: País e Estado travados pelas siglas oficiais)
        String filtro = String.format("country:%s|administrative_area:%s",
                estado.getPais().getSigla(), estado.getSigla());
        Optional<GoogleGeocodeResponse> response = googleClient.buscarLugarComFiltro(termoBusca, filtro);

        //Todo: tratar exceção
        if (response.isEmpty() || !response.get().isSucesso()) return Collections.emptyList();

        // 3. Extração e Upsert (Cidades podem vir como locality ou admin_area_level_2)
        AddressComponent componente = extrairComponente(response.get().results().get(0),
                "locality", "administrative_area_level_2");
        //Todo: tratar exceção
        if (componente == null) return Collections.emptyList();

        //Verificação Upsert
        Cidade cidadeSalva = cidadeRepository.findByNomeAndEstado(componente.longName(), estado)
                .orElseGet(() -> cidadeRepository.save(new Cidade(componente.longName(), estado)));

        return List.of(CidadeResponseDTO.daEntidade(cidadeSalva));
    }

    /**
     * Varre a lista de componentes do Google em busca das tags oficiais desejadas.
     */
    private AddressComponent extrairComponente(GoogleResult resultado, String... tiposDesejados) {
        List<String> alvos = List.of(tiposDesejados);

        for (AddressComponent componente : resultado.addressComponents()) {
            for (String tipo : componente.types()) {
                if (alvos.contains(tipo)) {
                    return componente; // Retorna o primeiro que der "match" com as tags do Google
                }
            }
        }
        return null;
    }
}