package com.QueroTrabalhar.domain.dtos.oportunidadeDeEmprego;

import com.QueroTrabalhar.domain.enums.Modalidade;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record OportunidadeDeEmpregoFilterDTO(
        @Size(max = 500, message = "O termo de busca deve ter no maximo 500 caracteres.")
        String termo,

        @Positive(message = "O tipo de emprego informado e invalido.")
        Long tipoDeEmpregoId,

        @Positive(message = "A empresa informada e invalida.")
        Long empresaId,

        @Positive(message = "O recrutador informado e invalido.")
        Long recrutadorId,

        @Positive(message = "O pais informado e invalido.")
        Long paisId,

        @Positive(message = "O estado informado e invalido.")
        Long estadoId,

        @Positive(message = "A cidade informada e invalida.")
        Long cidadeId,

        @Size(max = 20, message = "O status da localidade deve ter no maximo 20 caracteres.")
        String statusLocalidade,

        Modalidade modalidade
) {
}
