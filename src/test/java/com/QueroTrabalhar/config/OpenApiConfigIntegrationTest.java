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

    @Test
    void deveDocumentarEmpresasEOportunidadesNaOpenApi() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/empresas'].get.summary").value("Listar empresas públicas"))
                .andExpect(jsonPath("$.paths['/api/empresas'].get.description").value("Lista empresas públicas com paginação e filtros. Aceita os filtros termo, paisId, estadoId e cidadeId. A paginação usa os parâmetros page, size e sort, com padrão page=0, size=10 e sort=nome,asc. Os filtros de localidade consideram apenas empresas com localidade validada; empresas com localidade pendente ou sem localidade validada não aparecem neste endpoint."))
                .andExpect(jsonPath("$.paths['/api/empresas'].get.security").isEmpty())
                .andExpect(jsonPath("$.paths['/api/empresas'].post.responses['401'].description").value("Não autenticado."))
                .andExpect(jsonPath("$.paths['/api/empresas'].post.responses['422'].description").value("Regra de negócio violada ao cadastrar a empresa."))
                .andExpect(jsonPath("$.paths['/api/empresas/{id}'].get.responses['404'].description").value("Empresa não encontrada ou indisponível nos endpoints públicos."))
                .andExpect(jsonPath("$.paths['/api/empresas/{id}/oportunidades'].get.description").value("Lista as oportunidades públicas de uma empresa com paginação e filtros. Aceita os filtros termo, tipoDeEmpregoId, recrutadorId, paisId, estadoId, cidadeId e modalidade. O escopo da empresa é definido pelo parâmetro de caminho id. A paginação usa os parâmetros page, size e sort, com padrão page=0, size=10 e sort=id,desc. Os filtros de localidade consideram apenas oportunidades com localidade validada; oportunidades com localidade pendente ou sem localidade validada não aparecem neste endpoint."))
                .andExpect(jsonPath("$.paths['/api/empresas/{id}/oportunidades'].get.parameters[?(@.name=='cidadeId')].description").value(hasItem("Filtra oportunidades públicas da empresa por cidade. Considera apenas recursos com localidade validada.")))
                .andExpect(jsonPath("$.paths['/api/oportunidades'].get.summary").value("Listar oportunidades públicas"))
                .andExpect(jsonPath("$.paths['/api/oportunidades'].get.security").isEmpty())
                .andExpect(jsonPath("$.paths['/api/oportunidades'].get.parameters[?(@.name=='modalidade')].description").value(hasItem("Filtra oportunidades públicas pela modalidade.")))
                .andExpect(jsonPath("$.paths['/api/oportunidades/{id}'].get.responses['404'].description").value("Oportunidade não encontrada ou indisponível nos endpoints públicos."))
                .andExpect(jsonPath("$.paths['/api/oportunidades'].post.responses['401'].description").value("Não autenticado."))
                .andExpect(jsonPath("$.paths['/api/oportunidades'].post.responses['422'].description").value("Regra de negócio violada ao cadastrar a oportunidade."))
                .andExpect(jsonPath("$.paths['/api/oportunidades/{id}'].put.description").value("Atualiza uma oportunidade do recrutador autenticado. Nesta subfase, o contexto original de publicação é preservado: o payload não converte a oportunidade entre publicação pessoal e publicação em nome de empresa. A localidade pode ser atualizada por IDs estruturados ou por texto livre; se ficar pendente, a resposta interna retorna os campos de status para o dono do recurso e a oportunidade deixa de aparecer nos endpoints públicos até possuir localidade validada."))
                .andExpect(jsonPath("$.paths['/api/oportunidades/{id}'].delete.responses['422'].description").value("Regra de negócio violada ao remover a oportunidade."))
                .andExpect(jsonPath("$.components.schemas.EmpresaRequestDTO.properties.localidadeTexto.description").value("Texto livre da localidade. Use quando não houver IDs estruturados. Se a resolução automática não validar a localidade, a empresa será criada com localidade pendente no fluxo interno e não aparecerá nos endpoints públicos. A confirmação manual da sugestão ainda não está implementada no MVP."))
                .andExpect(jsonPath("$.components.schemas.EmpresaResponseDTO.properties.statusLocalidade.description").value("Status interno da localidade da empresa. Campo de apoio ao dono do recurso e não utilizado nos endpoints públicos."))
                .andExpect(jsonPath("$.components.schemas.OportunidadeDeEmpregoResponseDTO.properties.statusValidacaoLocalidade.description").value("Status detalhado da validação interna da localidade pendente. Campo interno do dono do recurso."))
                .andExpect(jsonPath("$.components.schemas.RecrutadorDaEmpresaResponseDTO.properties.statusVinculoEmpresa.description").value("Status atual do vínculo do recrutador com a empresa."));
    }
}
