package com.QueroTrabalhar.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class OpenApiConfigIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveExporConfiguracaoBaseDaOpenApiNoProfileLocal() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.info.title").value("Quero Trabalhar API"))
                .andExpect(jsonPath("$.info.description").value("API REST para gestão de empregabilidade, conectando candidatos, recrutadores, empresas, oportunidades de emprego, localidades e indicações."))
                .andExpect(jsonPath("$.info.version").value("v1"))
                .andExpect(jsonPath("$.security[0].bearerAuth").isArray())
                .andExpect(jsonPath("$.components.securitySchemes.bearerAuth.type").value("http"))
                .andExpect(jsonPath("$.components.securitySchemes.bearerAuth.scheme").value("bearer"))
                .andExpect(jsonPath("$.components.securitySchemes.bearerAuth.bearerFormat").value("JWT"))
                .andExpect(jsonPath("$.paths['/login'].post.summary").value("Autenticar usuário"))
                .andExpect(jsonPath("$.paths['/login'].post.description").value("Autentica o usuário e retorna o token JWT no header Authorization."))
                .andExpect(jsonPath("$.paths['/login'].post.security").isEmpty())
                .andExpect(jsonPath("$.paths['/login'].post.requestBody.required").value(true))
                .andExpect(jsonPath("$.paths['/login'].post.requestBody.content['application/json'].schema['$ref']").value("#/components/schemas/CredentialsDTO"))
                .andExpect(jsonPath("$.paths['/login'].post.responses['200'].description").value("Autenticado com sucesso."))
                .andExpect(jsonPath("$.paths['/login'].post.responses['200'].headers.Authorization.description").value("Token JWT no formato Bearer."))
                .andExpect(jsonPath("$.paths['/login'].post.responses['401'].description").value("E-mail ou senha inválidos."))
                .andExpect(jsonPath("$.paths['/api/localidades/paises'].get.security").isEmpty())
                .andExpect(jsonPath("$.components.schemas.CredentialsDTO.properties.email.format").value("email"))
                .andExpect(jsonPath("$.components.schemas.StandardError.properties.message.example").value("E-mail ou senha inválidos."));
    }

    @Test
    void deveDocumentarUsuariosAutenticacaoEErrosNaOpenApi() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/usuarios/cadastrar'].post.summary").value("Cadastrar usuário"))
                .andExpect(jsonPath("$.paths['/api/usuarios/cadastrar'].post.security").isEmpty())
                .andExpect(jsonPath("$.paths['/api/usuarios/cadastrar'].post.responses['409'].description").value("Já existe um usuário com o e-mail informado."))
                .andExpect(jsonPath("$.paths['/api/usuarios/me'].get.responses['401'].description").value("Não autenticado."))
                .andExpect(jsonPath("$.paths['/api/usuarios/me/senha'].put.responses['422'].description").value("Regra de negócio violada ao alterar a senha."))
                .andExpect(jsonPath("$.paths['/api/admin/usuarios'].get.summary").value("Listar usuários"))
                .andExpect(jsonPath("$.paths['/api/admin/usuarios'].get.description").value("Lista usuários com paginação e filtros. Aceita os filtros termo, nome, email, cpf, temPerfilCandidato e temPerfilRecrutador. A paginação usa os parâmetros page, size e sort, com padrão page=0, size=10 e sort=nome,asc."))
                .andExpect(jsonPath("$.paths['/api/admin/usuarios/{id}'].get.responses['403'].description").value("Sem permissão para acessar este recurso."))
                .andExpect(jsonPath("$.components.schemas.UsuarioRequestDTO.properties.senha.format").value("password"))
                .andExpect(jsonPath("$.paths['/api/admin/usuarios'].get.parameters[?(@.name=='temPerfilCandidato')].description").value(hasItem("Filtra usuários que possuem ou não perfil de candidato.")))
                .andExpect(jsonPath("$.paths['/api/admin/usuarios'].get.parameters[?(@.name=='temPerfilRecrutador')].description").value(hasItem("Filtra usuários que possuem ou não perfil de recrutador.")))
                .andExpect(jsonPath("$.components.schemas.ValidationError.properties.erros.type").value("array"))
                .andExpect(jsonPath("$.components.schemas.FieldMessage.properties.fieldName.description").value("Nome do campo com erro."));
    }
}
