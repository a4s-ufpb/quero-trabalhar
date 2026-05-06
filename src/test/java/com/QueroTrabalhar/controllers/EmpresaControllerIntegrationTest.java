package com.QueroTrabalhar.controllers;

import com.QueroTrabalhar.domain.entity.Empresa;
import com.QueroTrabalhar.domain.entity.localidade.Localidade;
import com.QueroTrabalhar.domain.entity.localidade.LocalidadePendente;
import com.QueroTrabalhar.domain.entity.localidade.Pais;
import com.QueroTrabalhar.repository.EmpresaRepository;
import com.QueroTrabalhar.repository.LocalidadePendenteRepository;
import com.QueroTrabalhar.repository.PaisRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class EmpresaControllerIntegrationTest {

    private static final AtomicInteger SEQUENCIA = new AtomicInteger();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private PaisRepository paisRepository;

    @Autowired
    private LocalidadePendenteRepository localidadePendenteRepository;

    @BeforeEach
    void limparSliceDeEmpresa() {
        empresaRepository.deleteAll();
        empresaRepository.flush();
        localidadePendenteRepository.deleteAll();
        localidadePendenteRepository.flush();
    }

    @Test
    void naoDeveListarEmpresasComLocalidadePendenteNaListagemPublica() throws Exception {
        int indice = proximoIndice();
        Empresa empresaValidada = persistirEmpresaValidada("Empresa Alpha " + indice);
        persistirEmpresaPendente("Empresa Oculta " + indice, "Regiao nao mapeada " + indice);

        mockMvc.perform(get("/api/empresas"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(empresaValidada.getId()))
                .andExpect(jsonPath("$[0].nome").value(empresaValidada.getNome()))
                .andExpect(jsonPath("$[0].statusLocalidade").value("VALIDADA"))
                .andExpect(jsonPath("$[*].statusLocalidade", everyItem(is("VALIDADA"))));
    }

    @Test
    void deveListarEmpresasPublicasOrdenadasPorNomeQuandoPossuemLocalidadeValidada() throws Exception {
        int indice = proximoIndice();
        Empresa empresaBeta = persistirEmpresaValidada("Empresa Beta " + indice);
        Empresa empresaAlpha = persistirEmpresaValidada("Empresa Alpha " + indice);

        mockMvc.perform(get("/api/empresas"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(empresaAlpha.getId()))
                .andExpect(jsonPath("$[0].nome").value(empresaAlpha.getNome()))
                .andExpect(jsonPath("$[1].id").value(empresaBeta.getId()))
                .andExpect(jsonPath("$[1].nome").value(empresaBeta.getNome()));
    }

    private int proximoIndice() {
        return SEQUENCIA.incrementAndGet();
    }

    private Empresa persistirEmpresaValidada(String nome) {
        Pais pais = paisRepository.findFirstBySiglaIgnoreCase("BR")
                .orElseThrow(() -> new IllegalStateException("Pais BR nao encontrado no seed de teste."));

        Empresa empresa = new Empresa(nome, "Descricao publica", null, null, null, new Localidade(pais));
        return empresaRepository.saveAndFlush(empresa);
    }

    private Empresa persistirEmpresaPendente(String nome, String localidadeTextoOriginal) {
        LocalidadePendente localidadePendente = localidadePendenteRepository.saveAndFlush(
                LocalidadePendente.criarPendenteInformadaPeloUsuario(
                        localidadeTextoOriginal,
                        "Aguardando validacao"
                )
        );

        Empresa empresa = new Empresa(nome, "Descricao pendente", null, null, null, null);
        empresa.definirLocalidadePendente(localidadePendente);
        return empresaRepository.saveAndFlush(empresa);
    }
}
