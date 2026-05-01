package com.QueroTrabalhar.controllers;


import com.QueroTrabalhar.domain.dtos.indicacao.IndicacaoRequestDTO;
import com.QueroTrabalhar.domain.dtos.indicacao.IndicacaoResponseDTO;
import com.QueroTrabalhar.services.IndicacaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/indicacoes")
public class IndicacaoController {

    private final IndicacaoService indicacaoService;

    public IndicacaoController(IndicacaoService indicacaoService) {
        this.indicacaoService = indicacaoService;
    }

    @PostMapping
    public ResponseEntity<IndicacaoResponseDTO> criarIndicacao(@RequestBody @Valid IndicacaoRequestDTO indicacaoRequestDTO) {
        IndicacaoResponseDTO indicacaoCriada = indicacaoService.criarIndicacao(indicacaoRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(indicacaoCriada);
    }

    @GetMapping("/me/dadas")
    public ResponseEntity<List<IndicacaoResponseDTO>> listarMinhasIndicacoesDadas() {
        return ResponseEntity.ok(indicacaoService.listarIndicacoesDadasPeloUsuarioAutenticado());
    }

    @GetMapping("/me/recebidas")
    public ResponseEntity<List<IndicacaoResponseDTO>> listarMinhasIndicacoesRecebidas() {
        return ResponseEntity.ok(indicacaoService.listarIndicacoesRecebidasPeloUsuarioAutenticado());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarMinhaIndicacao(@PathVariable Long id) {
        indicacaoService.excluirIndicacaoDoUsuarioAutenticado(id);
        return ResponseEntity.noContent().build();
    }
}
