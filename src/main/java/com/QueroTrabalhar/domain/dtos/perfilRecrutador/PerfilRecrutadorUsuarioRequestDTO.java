package com.QueroTrabalhar.domain.dtos.perfilRecrutador;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "PerfilRecrutadorUsuarioRequestDTO", description = "Dados para adicionar perfil de recrutador a um usuário em fluxo administrativo.")
public record PerfilRecrutadorUsuarioRequestDTO(
        @NotBlank(message = "O nome da empresa é obrigatório.")
        @Size(max = 100, message = "O nome da empresa deve ter no máximo 100 caracteres.")
        @Schema(description = "Nome legado da empresa informado no momento de criação do perfil de recrutador.", example = "Quero Trabalhar", requiredMode = Schema.RequiredMode.REQUIRED)
        String nomeDaEmpresa
) {
}
