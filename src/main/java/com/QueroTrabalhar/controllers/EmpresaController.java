package com.QueroTrabalhar.controllers;

import com.QueroTrabalhar.domain.dtos.empresa.EmpresaRequestDTO;
import com.QueroTrabalhar.domain.dtos.empresa.EmpresaPublicaResponseDTO;
import com.QueroTrabalhar.domain.dtos.empresa.EmpresaResponseDTO;
import com.QueroTrabalhar.domain.dtos.oportunidadeDeEmprego.OportunidadeDeEmpregoPublicaResponseDTO;
import com.QueroTrabalhar.domain.dtos.oportunidadeDeEmprego.OportunidadeDeEmpregoResponseDTO;
import com.QueroTrabalhar.domain.dtos.perfilRecrutador.RecrutadorDaEmpresaResponseDTO;
import com.QueroTrabalhar.services.EmpresaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/empresas")
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @PostMapping
    public ResponseEntity<EmpresaResponseDTO> criarEmpresa(@RequestBody @Valid EmpresaRequestDTO empresaRequestDTO) {
        EmpresaResponseDTO empresaCriada = empresaService.criarEmpresa(empresaRequestDTO);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(empresaCriada.id())
                .toUri();

        return ResponseEntity.created(uri).body(empresaCriada);
    }

    @GetMapping
    public ResponseEntity<List<EmpresaPublicaResponseDTO>> listarEmpresas() {
        return ResponseEntity.ok(empresaService.listarEmpresas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpresaPublicaResponseDTO> buscarEmpresaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(empresaService.buscarEmpresaPorId(id));
    }

    @GetMapping("/{id}/recrutadores")
    public ResponseEntity<List<RecrutadorDaEmpresaResponseDTO>> listarRecrutadoresDaEmpresa(@PathVariable Long id) {
        return ResponseEntity.ok(empresaService.listarRecrutadoresAprovadosDaEmpresa(id));
    }

    @GetMapping("/{id}/oportunidades")
    public ResponseEntity<List<OportunidadeDeEmpregoPublicaResponseDTO>> listarOportunidadesDaEmpresa(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(empresaService.listarOportunidadesDaEmpresa(id));
    }
}
