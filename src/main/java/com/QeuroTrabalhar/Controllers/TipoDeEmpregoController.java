package com.QeuroTrabalhar.Controllers;

import com.QeuroTrabalhar.Entity.TipoDeEmprego; // Importa a entidade TipoDeEmprego
import com.QeuroTrabalhar.Services.TipoDeEmpregoService; // Importa o serviço que contém a lógica de negócio
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity; // Utilizado para retornar respostas HTTP
import org.springframework.web.bind.annotation.*; // Importa as anotações para criar um controlador REST

import java.util.List; // Importa List para lidar com coleções de objetos
import java.util.Optional; // Importa Optional para evitar NullPointerException

@RestController // Indica que esta classe é um controlador REST
@RequestMapping ("api/tipos-de-emprego") // Define a URL base para endpoints deste controlador
public class TipoDeEmpregoController {

    @Autowired // Injeta as instâncias
    private TipoDeEmpregoService tipoDeEmpregoService; // Dependencia do Serviço

    // Metodo para listar todos os Tipos de Emprego (GET)
    @GetMapping
    public List<TipoDeEmprego> listarTodos() {
        return tipoDeEmpregoService.listarTodosOsTiposDeEmprego(); // Chama o serviço para obter a lista do banco de dados
    }

    // Metodo para buscar um Tipo de Emprego pelo ID (GET com parâmetro)
    @GetMapping("/{id}")
    public ResponseEntity<TipoDeEmprego> buscarPorId(@PathVariable Long id) {
        Optional<TipoDeEmprego> tipoDeEmprego = tipoDeEmpregoService.buscarPorId(id);
        return tipoDeEmprego.map(ResponseEntity::ok) // se encontrado, retorna 200 OK com objeto
                .orElseGet(() -> ResponseEntity.notFound().build()); // se não encontrado, retorna 404 Not Found
    }

    // Metodo para criar um novo tipo de Emprego (POST)
    @PostMapping
    public TipoDeEmprego criar(@RequestBody TipoDeEmprego tipoDeEmprego) {
        return tipoDeEmpregoService.salvar(tipoDeEmprego); // Chama o serviço para salvar no banco
    }

    // Metodo para deletar um tipo de emprego do banco pelo ID

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        tipoDeEmpregoService.deletar(id); // Chama o serviço para excluir pelo ID
        return ResponseEntity.noContent().build(); // Retorna status 204 (No Content), indicando sucesso sem resposta
    }
}
