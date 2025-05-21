package com.QueroTrabalhar.Controllers;


import com.QueroTrabalhar.Entity.OportunidadeDeEmprego;
import com.QueroTrabalhar.Services.OportunidadeDeEmpregoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/oportunidadeDeEmprego")
public class OportunidadeDeEmpregoController {

    @Autowired
    private OportunidadeDeEmpregoService oportunidadeDeEmpregoService;

    @GetMapping
    public List<OportunidadeDeEmprego> listar() {
        return oportunidadeDeEmpregoService.listarTodasOportunidadeDeEmprego();
    }

    @GetMapping ("/{id}")
    public ResponseEntity<OportunidadeDeEmprego> buscarOportunidadeDeEmpregoPorId(@PathVariable Long id) {
        Optional<OportunidadeDeEmprego> OportunidadeDeEmprego = oportunidadeDeEmpregoService.buscarPorId(id);

        return OportunidadeDeEmprego.map(ResponseEntity :: ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public OportunidadeDeEmprego salvarOportunidadeDeEmprego(@RequestBody OportunidadeDeEmprego oportunidadeDeEmprego) {
        return oportunidadeDeEmpregoService.salvarOportunidadeDeEmprego(oportunidadeDeEmprego);
    }

    @DeleteMapping ("/{id}")
    public void removerOportunidadeDeEmprego(@PathVariable Long id) {
        oportunidadeDeEmpregoService.removerOportunidadeDeEmprego(id);
    }
}
