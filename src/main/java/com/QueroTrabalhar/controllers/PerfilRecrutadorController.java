package com.QueroTrabalhar.controllers;

import com.QueroTrabalhar.domain.dtos.oportunidadeDeEmprego.OportunidadeDeEmpregoResponseDTO;
import com.QueroTrabalhar.domain.dtos.perfilRecrutador.PerfilRecrutadorEmpresaResponseDTO;
import com.QueroTrabalhar.domain.dtos.perfilRecrutador.PerfilRecrutadorResponseDTO;
import com.QueroTrabalhar.services.OportunidadeDeEmpregoService;
import com.QueroTrabalhar.services.PerfilRecrutadorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recrutadores")
public class PerfilRecrutadorController {

    private final PerfilRecrutadorService recrutadorService;
    private final OportunidadeDeEmpregoService oportunidadeDeEmpregoService;

    public PerfilRecrutadorController(
            PerfilRecrutadorService recrutadorService,
            OportunidadeDeEmpregoService oportunidadeDeEmpregoService
    ) {
        this.recrutadorService = recrutadorService;
        this.oportunidadeDeEmpregoService = oportunidadeDeEmpregoService;
    }

    @GetMapping("/me")
    public ResponseEntity<PerfilRecrutadorResponseDTO> buscarMeuPerfil() {
        return ResponseEntity.ok(recrutadorService.buscarMeuPerfil());
    }

    @GetMapping("/me/oportunidades")
    public ResponseEntity<List<OportunidadeDeEmpregoResponseDTO>> listarMinhasOportunidades() {
        return ResponseEntity.ok(oportunidadeDeEmpregoService.listarOportunidadesDoRecrutadorAutenticado());
    }

    @PostMapping("/me/empresa/{empresaId}/solicitar-vinculo")
    public ResponseEntity<PerfilRecrutadorEmpresaResponseDTO> solicitarVinculoEmpresa(@PathVariable Long empresaId) {
        return ResponseEntity.ok(recrutadorService.solicitarVinculoEmpresa(empresaId));
    }

    @GetMapping("/me/empresa")
    public ResponseEntity<PerfilRecrutadorEmpresaResponseDTO> buscarMinhaEmpresa() {
        return ResponseEntity.ok(recrutadorService.buscarMinhaEmpresa());
    }
}
