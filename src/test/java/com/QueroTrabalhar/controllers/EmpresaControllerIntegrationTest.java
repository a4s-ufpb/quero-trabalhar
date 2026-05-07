package com.QueroTrabalhar.controllers;

import com.QueroTrabalhar.domain.entity.Empresa;
import com.QueroTrabalhar.domain.entity.OportunidadeDeEmprego;
import com.QueroTrabalhar.domain.entity.PerfilRecrutador;
import com.QueroTrabalhar.domain.entity.TipoDeEmprego;
import com.QueroTrabalhar.domain.entity.Usuario;
import com.QueroTrabalhar.domain.entity.localidade.Localidade;
import com.QueroTrabalhar.domain.entity.localidade.LocalidadePendente;
import com.QueroTrabalhar.domain.entity.localidade.Pais;
import com.QueroTrabalhar.repository.EmpresaRepository;
import com.QueroTrabalhar.repository.LocalidadePendenteRepository;
import com.QueroTrabalhar.repository.OportunidadeDeEmpregoRepository;
import com.QueroTrabalhar.repository.PaisRepository;
import com.QueroTrabalhar.repository.TipoDeEmpregoRepository;
import com.QueroTrabalhar.repository.UsuarioRepository;
import com.QueroTrabalhar.domain.enums.CampoLocalidadePendente;
import com.QueroTrabalhar.domain.enums.Modalidade;
import com.QueroTrabalhar.domain.enums.TipoRecursoLocalidadePendente;
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

import static org.hamcrest.Matchers.hasSize;
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
    private OportunidadeDeEmpregoRepository oportunidadeDeEmpregoRepository;

    @Autowired
    private PaisRepository paisRepository;

    @Autowired
    private TipoDeEmpregoRepository tipoDeEmpregoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private LocalidadePendenteRepository localidadePendenteRepository;

    @BeforeEach
    void limparSliceDeEmpresa() {
        for (OportunidadeDeEmprego oportunidade : oportunidadeDeEmpregoRepository.findAll()) {
            oportunidadeDeEmpregoRepository.removerTodosInteressesDaVaga(oportunidade.getId());
        }
        oportunidadeDeEmpregoRepository.deleteAll();
        oportunidadeDeEmpregoRepository.flush();
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
                .andExpect(jsonPath("$[0].statusLocalidade").doesNotExist())
                .andExpect(jsonPath("$[0].localidadeTextoOriginal").doesNotExist())
                .andExpect(jsonPath("$[0].statusValidacaoLocalidade").doesNotExist())
                .andExpect(jsonPath("$[0].motivoPendenciaLocalidade").doesNotExist());
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
                .andExpect(jsonPath("$[1].nome").value(empresaBeta.getNome()))
                .andExpect(jsonPath("$[0].statusLocalidade").doesNotExist())
                .andExpect(jsonPath("$[0].localidadeTextoOriginal").doesNotExist())
                .andExpect(jsonPath("$[0].statusValidacaoLocalidade").doesNotExist())
                .andExpect(jsonPath("$[0].motivoPendenciaLocalidade").doesNotExist());
    }

    @Test
    void deveBuscarEmpresaPublicaSemCamposTecnicosDePendencia() throws Exception {
        int indice = proximoIndice();
        Empresa empresa = persistirEmpresaValidada("Empresa Publica " + indice);

        mockMvc.perform(get("/api/empresas/{id}", empresa.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(empresa.getId()))
                .andExpect(jsonPath("$.nome").value(empresa.getNome()))
                .andExpect(jsonPath("$.statusLocalidade").doesNotExist())
                .andExpect(jsonPath("$.localidadeTextoOriginal").doesNotExist())
                .andExpect(jsonPath("$.statusValidacaoLocalidade").doesNotExist())
                .andExpect(jsonPath("$.motivoPendenciaLocalidade").doesNotExist());
    }

    @Test
    void naoDeveRetornarEmpresaComLocalidadePendenteNoDetalhePublico() throws Exception {
        int indice = proximoIndice();
        Empresa empresaPendente = persistirEmpresaPendente(
                "Empresa Oculta Detalhe " + indice,
                "Regiao nao mapeada detalhe " + indice
        );

        mockMvc.perform(get("/api/empresas/{id}", empresaPendente.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void naoDevePermitirNavegacaoPublicaDeRecrutadoresParaEmpresaComLocalidadePendente() throws Exception {
        int indice = proximoIndice();
        Empresa empresaPendente = persistirEmpresaPendente(
                "Empresa Oculta Recrutadores " + indice,
                "Regiao nao mapeada recrutadores " + indice
        );

        mockMvc.perform(get("/api/empresas/{id}/recrutadores", empresaPendente.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void naoDeveListarOportunidadesComLocalidadePendenteNaListagemPublicaDaEmpresa() throws Exception {
        int indice = proximoIndice();
        Empresa empresa = persistirEmpresaValidada("Empresa Vagas " + indice);
        TipoDeEmprego tipoDeEmprego = persistirTipoDeEmprego("Backend empresa", indice);
        PerfilRecrutador recrutador = persistirRecrutador(indice, "Marina Lima");

        OportunidadeDeEmprego oportunidadeValidada = persistirOportunidadeValidada(
                "API publica",
                Modalidade.REMOTO,
                tipoDeEmprego,
                recrutador,
                empresa
        );
        persistirOportunidadePendente(
                "Operacao em regiao nao mapeada",
                Modalidade.HIBRIDO,
                tipoDeEmprego,
                recrutador,
                empresa,
                "Regiao nao mapeada " + indice
        );

        mockMvc.perform(get("/api/empresas/{id}/oportunidades", empresa.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(oportunidadeValidada.getId()))
                .andExpect(jsonPath("$[0].descricao").value(oportunidadeValidada.getDescricao()))
                .andExpect(jsonPath("$[0].statusLocalidade").doesNotExist())
                .andExpect(jsonPath("$[0].localidadeTextoOriginal").doesNotExist())
                .andExpect(jsonPath("$[0].statusValidacaoLocalidade").doesNotExist())
                .andExpect(jsonPath("$[0].motivoPendenciaLocalidade").doesNotExist());
    }

    private int proximoIndice() {
        return SEQUENCIA.incrementAndGet();
    }

    private TipoDeEmprego persistirTipoDeEmprego(String tituloBase, int indice) {
        return tipoDeEmpregoRepository.saveAndFlush(
                TipoDeEmprego.criarTipoDeEmpregoAdmin(
                        tituloBase + " " + indice,
                        tituloBase + " descricao " + indice
                )
        );
    }

    private PerfilRecrutador persistirRecrutador(int indice, String nome) {
        Usuario usuario = new Usuario(
                gerarCpfValido(indice),
                nome,
                String.format("839%08d", indice),
                "empresa-controller" + indice + "@teste.com",
                "Senha@123"
        );
        usuario.adicionarPerfilRecrutador(new PerfilRecrutador(null, "Empresa Legada " + indice));
        return usuarioRepository.saveAndFlush(usuario).getPerfilRecrutador();
    }

    private Empresa persistirEmpresaValidada(String nome) {
        Pais pais = paisRepository.findFirstBySiglaIgnoreCase("BR")
                .orElseThrow(() -> new IllegalStateException("Pais BR nao encontrado no seed de teste."));

        Empresa empresa = new Empresa(nome, "Descricao publica", null, null, null, new Localidade(pais));
        return empresaRepository.saveAndFlush(empresa);
    }

    private Empresa persistirEmpresaPendente(String nome, String localidadeTextoOriginal) {
        Empresa empresa = new Empresa(nome, "Descricao pendente", null, null, null, null);
        Empresa empresaSalva = empresaRepository.saveAndFlush(empresa);
        localidadePendenteRepository.saveAndFlush(
                LocalidadePendente.criarPendenteInformadaPeloUsuario(
                        localidadeTextoOriginal,
                        "Aguardando validacao",
                        TipoRecursoLocalidadePendente.EMPRESA,
                        empresaSalva.getId(),
                        CampoLocalidadePendente.LOCALIDADE
                )
        );
        return empresaSalva;
    }

    private OportunidadeDeEmprego persistirOportunidadeValidada(
            String descricao,
            Modalidade modalidade,
            TipoDeEmprego tipoDeEmprego,
            PerfilRecrutador recrutador,
            Empresa empresa
    ) {
        Pais pais = paisRepository.findFirstBySiglaIgnoreCase("BR")
                .orElseThrow(() -> new IllegalStateException("Pais BR nao encontrado no seed de teste."));

        OportunidadeDeEmprego oportunidade = new OportunidadeDeEmprego(
                descricao,
                tipoDeEmprego,
                modalidade,
                new Localidade(pais),
                recrutador,
                empresa
        );
        return oportunidadeDeEmpregoRepository.saveAndFlush(oportunidade);
    }

    private OportunidadeDeEmprego persistirOportunidadePendente(
            String descricao,
            Modalidade modalidade,
            TipoDeEmprego tipoDeEmprego,
            PerfilRecrutador recrutador,
            Empresa empresa,
            String textoOriginal
    ) {
        OportunidadeDeEmprego oportunidade = new OportunidadeDeEmprego(
                descricao,
                tipoDeEmprego,
                modalidade,
                null,
                recrutador,
                empresa
        );
        OportunidadeDeEmprego oportunidadeSalva = oportunidadeDeEmpregoRepository.saveAndFlush(oportunidade);
        localidadePendenteRepository.saveAndFlush(
                LocalidadePendente.criarPendenteInformadaPeloUsuario(
                        textoOriginal,
                        "Aguardando validacao",
                        TipoRecursoLocalidadePendente.OPORTUNIDADE_DE_EMPREGO,
                        oportunidadeSalva.getId(),
                        CampoLocalidadePendente.LOCALIDADE
                )
        );
        return oportunidadeSalva;
    }

    private String gerarCpfValido(int indice) {
        String base = String.format("%09d", indice);
        int primeiroDigito = calcularDigitoCpf(base, 10);
        int segundoDigito = calcularDigitoCpf(base + primeiroDigito, 11);
        return base + primeiroDigito + segundoDigito;
    }

    private int calcularDigitoCpf(String base, int pesoInicial) {
        int soma = 0;
        int peso = pesoInicial;

        for (char caractere : base.toCharArray()) {
            soma += Character.getNumericValue(caractere) * peso;
            peso--;
        }

        int resto = 11 - (soma % 11);
        return resto >= 10 ? 0 : resto;
    }
}
