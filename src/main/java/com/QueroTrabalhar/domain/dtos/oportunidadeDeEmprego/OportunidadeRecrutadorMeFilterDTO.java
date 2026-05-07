package com.QueroTrabalhar.domain.dtos.oportunidadeDeEmprego;

import com.QueroTrabalhar.domain.enums.Modalidade;
import com.QueroTrabalhar.domain.enums.StatusLocalidadeFiltro;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record OportunidadeRecrutadorMeFilterDTO(
        @Size(max = 500, message = "O termo de busca deve ter no maximo 500 caracteres.")
        String termo,

        @Positive(message = "O tipo de emprego informado e invalido.")
        Long tipoDeEmpregoId,

        @Positive(message = "A empresa informada e invalida.")
        Long empresaId,

        @Positive(message = "O pais informado e invalido.")
        Long paisId,

        @Positive(message = "O estado informado e invalido.")
        Long estadoId,

        @Positive(message = "A cidade informada e invalida.")
        Long cidadeId,

        Modalidade modalidade,

        StatusLocalidadeFiltro statusLocalidade
) {
}
