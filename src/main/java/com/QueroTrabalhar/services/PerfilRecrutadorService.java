package com.QueroTrabalhar.services;

import com.QueroTrabalhar.domain.dtos.oportunidadeDeEmprego.OportunidadeDeEmpregoRequestDTO;
import com.QueroTrabalhar.domain.dtos.oportunidadeDeEmprego.OportunidadeDeEmpregoResponseDTO;
import com.QueroTrabalhar.domain.entity.PerfilRecrutador;
import com.QueroTrabalhar.services.exceptions.BusinessRuleException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PerfilRecrutadorService {

    private final OportunidadeDeEmpregoService oportunidadeDeEmpregoService;

    public PerfilRecrutadorService(OportunidadeDeEmpregoService oportunidadeDeEmpregoService) {
        this.oportunidadeDeEmpregoService = oportunidadeDeEmpregoService;
    }
}
