package com.QueroTrabalhar.domain.dtos.oportunidadeDeEmprego;

import com.QueroTrabalhar.domain.enums.Modalidade;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record OportunidadeDeEmpregoFilterDTO(
        @Size(max = 500, message = "O termo de busca deve ter no máximo 500 caracteres.")
        String termo,

        @Positive(message = "O tipo de emprego informado é inválido.")
        Long tipoDeEmpregoId,

        @Positive(message = "A empresa informada é inválida.")
        Long empresaId,

        @Positive(message = "O recrutador informado é inválido.")
        Long recrutadorId,

        @Positive(message = "O país informado é inválido.")
        Long paisId,

        @Positive(message = "O estado informado é inválido.")
        Long estadoId,

        @Positive(message = "A cidade informada é inválida.")
        Long cidadeId,

        Modalidade modalidade
) {
}
