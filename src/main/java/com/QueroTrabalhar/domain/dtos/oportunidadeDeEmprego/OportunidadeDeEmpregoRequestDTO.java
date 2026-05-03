package com.QueroTrabalhar.domain.dtos.oportunidadeDeEmprego;

import com.QueroTrabalhar.domain.enums.Modalidade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record OportunidadeDeEmpregoRequestDTO(
        @NotBlank(message = "A descrição da oportunidade é obrigatória.")
        @Size(max = 500, message = "A descrição da oportunidade deve ter no máximo 500 caracteres.")
        String descricao,

        @NotNull(message = "O tipo de emprego é obrigatório.")
        @Positive(message = "O tipo de emprego informado é inválido.")
        Long tipoDeEmpregoId,

        @NotNull(message = "A modalidade é obrigatória.")
        Modalidade modalidade,

        @Positive(message = "O país informado é inválido.")
        Long paisId,

        @Positive(message = "O estado informado é inválido.")
        Long estadoId,

        @Positive(message = "A cidade informada é inválida.")
        Long cidadeId,

        @Size(max = 255, message = "O texto da localidade deve ter no máximo 255 caracteres.")
        String localidadeTexto,

        Boolean publicarComoEmpresa
) {
}
