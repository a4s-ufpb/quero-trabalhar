package com.QueroTrabalhar.controllers;

import com.QueroTrabalhar.domain.dtos.localidade.CidadeResponseDTO;
import com.QueroTrabalhar.domain.dtos.localidade.EstadoResponseDTO;
import com.QueroTrabalhar.domain.dtos.localidade.PaisResponseDTO;
import com.QueroTrabalhar.services.LocalidadeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/localidades")
public class LocalidadeController {


    private final LocalidadeService localidadeService;

    public LocalidadeController(LocalidadeService localidadeService) {
        this.localidadeService = localidadeService;
    }

    //GET /api/localidades/paises?termo={termo}
    @GetMapping("/paises")
    public ResponseEntity<List<PaisResponseDTO>> buscarPaises(
            @RequestParam(name = "termo", defaultValue = "") String termoBusca) {

        //Talvez mudar para o front (discutir com YASMIM)
        // Trava de Performance: Só busca se o usuário digitar pelo menos 2 letras
        if (termoBusca.trim().length() < 2) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        List<PaisResponseDTO> resultado = localidadeService.buscarPais(termoBusca.trim());
        return ResponseEntity.ok(resultado);
    }


    //GET /api/localidades/estados?paisId={id}&termo={termo}
    @GetMapping("/estados")
    public ResponseEntity<List<EstadoResponseDTO>> buscarEstados(
            @RequestParam(name = "paisId") Long paisId,
            @RequestParam(name = "termo", defaultValue = "") String termoBusca) {

        if (termoBusca.trim().length() < 2) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        List<EstadoResponseDTO> resultado = localidadeService.buscarEstado(paisId, termoBusca.trim());
        return ResponseEntity.ok(resultado);
    }


    //GET /api/localidades/cidades?estadoId={id}&termo={termo}
    @GetMapping("/cidades")
    public ResponseEntity<List<CidadeResponseDTO>> buscarCidades(
            @RequestParam(name = "estadoId") Long estadoId,
            @RequestParam(name = "termo", defaultValue = "") String termoBusca) {

        if (termoBusca.trim().length() < 2) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        List<CidadeResponseDTO> resultado = localidadeService.buscarCidade(estadoId, termoBusca.trim());
        return ResponseEntity.ok(resultado);
    }

}
