package com.QueroTrabalhar.controllers;

import com.QueroTrabalhar.domain.dtos.perfilRecrutador.PerfilRecrutadorEmpresaResponseDTO;
import com.QueroTrabalhar.services.PerfilRecrutadorService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/recrutadores")
@PreAuthorize("hasRole('ADMIN')")
public class PerfilRecrutadorAdminController {

    private final PerfilRecrutadorService perfilRecrutadorService;

    public PerfilRecrutadorAdminController(PerfilRecrutadorService perfilRecrutadorService) {
        this.perfilRecrutadorService = perfilRecrutadorService;
    }

    @PatchMapping("/{recrutadorId}/empresa/aprovar")
    public ResponseEntity<PerfilRecrutadorEmpresaResponseDTO> aprovarVinculoEmpresa(
            @PathVariable Long recrutadorId
    ) {
        return ResponseEntity.ok(perfilRecrutadorService.aprovarVinculoEmpresaComoAdmin(recrutadorId));
    }

    @PatchMapping("/{recrutadorId}/empresa/recusar")
    public ResponseEntity<PerfilRecrutadorEmpresaResponseDTO> recusarVinculoEmpresa(
            @PathVariable Long recrutadorId
    ) {
        return ResponseEntity.ok(perfilRecrutadorService.recusarVinculoEmpresaComoAdmin(recrutadorId));
    }
}
