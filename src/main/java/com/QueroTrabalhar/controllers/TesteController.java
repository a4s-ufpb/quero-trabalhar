package com.QueroTrabalhar.controllers;

import com.QueroTrabalhar.services.CidadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TesteController {
    @Autowired
    private CidadeService cidadeService;

    @GetMapping("/gerar-sql")
    public String gerar() {
        cidadeService.gerarScriptSqlDeTodasAsCidades();
        return "Olhe o console do IntelliJ!";
    }
}