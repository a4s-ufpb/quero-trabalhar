package com.QueroTrabalhar.services;

import com.QueroTrabalhar.domain.dtos.perfilRecrutador.PerfilRecrutadorEmpresaResponseDTO;
import com.QueroTrabalhar.domain.entity.Empresa;
import com.QueroTrabalhar.domain.entity.PerfilRecrutador;
import com.QueroTrabalhar.domain.enums.StatusVinculoEmpresa;
import com.QueroTrabalhar.repository.EmpresaRepository;
import com.QueroTrabalhar.repository.PerfilRecrutadorRepository;
import com.QueroTrabalhar.services.exceptions.BusinessRuleException;
import com.QueroTrabalhar.services.exceptions.ObjectNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PerfilRecrutadorService {

    private final PerfilRecrutadorRepository perfilRecrutadorRepository;
    private final EmpresaRepository empresaRepository;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public PerfilRecrutadorService(
            PerfilRecrutadorRepository perfilRecrutadorRepository,
            EmpresaRepository empresaRepository,
            UsuarioAutenticadoService usuarioAutenticadoService
    ) {
        this.perfilRecrutadorRepository = perfilRecrutadorRepository;
        this.empresaRepository = empresaRepository;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    @Transactional
    public PerfilRecrutadorEmpresaResponseDTO solicitarVinculoEmpresa(Long empresaId) {
        PerfilRecrutador perfilRecrutador = usuarioAutenticadoService.obterPerfilRecrutadorAutenticado();
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new ObjectNotFoundException("Empresa não encontrada. ID: " + empresaId));

        validarSolicitacaoDeVinculo(perfilRecrutador);

        perfilRecrutador.solicitarVinculoEmpresa(empresa);

        return PerfilRecrutadorEmpresaResponseDTO.daEntidade(
                perfilRecrutadorRepository.save(perfilRecrutador)
        );
    }

    @Transactional(readOnly = true)
    public PerfilRecrutadorEmpresaResponseDTO buscarMinhaEmpresa() {
        PerfilRecrutador perfilRecrutador = usuarioAutenticadoService.obterPerfilRecrutadorAutenticado();

        if (perfilRecrutador.getEmpresaVinculada() == null || perfilRecrutador.getStatusVinculoEmpresa() == null) {
            throw new BusinessRuleException("O recrutador autenticado não possui empresa vinculada no momento.");
        }

        return PerfilRecrutadorEmpresaResponseDTO.daEntidade(perfilRecrutador);
    }

    private void validarSolicitacaoDeVinculo(PerfilRecrutador perfilRecrutador) {
        StatusVinculoEmpresa statusVinculoEmpresa = perfilRecrutador.getStatusVinculoEmpresa();

        if (statusVinculoEmpresa == StatusVinculoEmpresa.PENDENTE
                || statusVinculoEmpresa == StatusVinculoEmpresa.APROVADO) {
            throw new BusinessRuleException(
                    "O recrutador autenticado já possui um vínculo pendente ou aprovado com uma empresa."
            );
        }
    }
}
