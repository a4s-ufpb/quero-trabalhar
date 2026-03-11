package com.QueroTrabalhar.controllers;

import com.QueroTrabalhar.services.LocalidadeSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/localidades")
public class LocalidadeAdminController {

    @Autowired
    private LocalidadeSyncService syncService;

    @PostMapping("/sync/{codigoUf}")
    public ResponseEntity<Void> sync(@PathVariable Integer codigoUf) {
        syncService.sincronizarEstado(codigoUf);
        return ResponseEntity.ok().build();
    }
}
