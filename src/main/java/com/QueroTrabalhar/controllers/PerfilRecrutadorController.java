package com.QueroTrabalhar.controllers;

import com.QueroTrabalhar.domain.dtos.oportunidadeDeEmprego.OportunidadeDeEmpregoRequestDTO;
import com.QueroTrabalhar.domain.dtos.oportunidadeDeEmprego.OportunidadeDeEmpregoResponseDTO;
import com.QueroTrabalhar.services.PerfilRecrutadorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/recrutadores")
public class PerfilRecrutadorController {

    private final PerfilRecrutadorService recrutadorService;

    public PerfilRecrutadorController(PerfilRecrutadorService recrutadorService) {
        this.recrutadorService = recrutadorService;
    }

    //@GetMapping("/me/vagas") /vagas para a url ficar mais semantica
    //endpoint que pegue as vagas com /me
}
