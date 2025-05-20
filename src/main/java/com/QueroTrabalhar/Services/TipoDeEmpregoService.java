package com.QueroTrabalhar.Services;  // Pacote onde esta classe está localizada


import com.QueroTrabalhar.Entity.TipoDeEmprego; // Importa a entidade TipoDeEmprego
import com.QueroTrabalhar.Repository.TipoDeEmpregoRepository; // Importa o repositório para acessar o banco de dados
import org.springframework.beans.factory.annotation.Autowired; // Importa anotação para injeção de classe
import org.springframework.stereotype.Service; // Indica que esta classe é um serviço gerenciado pelo Spring

import java.util.List; // Importa a classe List para lidar com listas de objetos
import java.util.Optional; // Importa a classe Optional para evitar NullPointerException

@Service // Marca esta classe como um serviço do Spring
public class TipoDeEmpregoService {

    @Autowired // Injeta as instâncias
    private TipoDeEmpregoRepository tipoDeEmpregoRepository; // Dependencia do repositório


    public List<TipoDeEmprego> listarTodosOsTiposDeEmprego() {
        return tipoDeEmpregoRepository.findAll(); // Retorna todos os registros do banco
    }

    // Metodo para buscar um Tipo de Emprego pelo ID
    public Optional<TipoDeEmprego> buscarPorId(Long id) {
        return tipoDeEmpregoRepository.findById(id); // Retorna um Optional, que pode estar vazio ou conter um objeto
    }

    // Metodo para salvar um novo Tipo de Emprego
    public TipoDeEmprego salvar(TipoDeEmprego tipoDeEmprego) { // TODO rever o tipo de retorno
        // Verifica se já existe um tipo de emprego com o mesmo nome
        Optional<TipoDeEmprego> existente = tipoDeEmpregoRepository.findByTitulo(tipoDeEmprego.getTitulo()); // cria um Objeto para guardar o possível Título
        if (existente.isPresent()) { // usa o metodo isPresent para verificar se o Titulo está em existente
            throw new IllegalArgumentException("Já existe um tipo de emprego com este nome!"); // TODO rever se deve lançar outra exceção ou retornar um Optional
        }
        return tipoDeEmpregoRepository.save(tipoDeEmprego); // Salva no banco e retorna o objeto salvo
    }

    // Metodo para Deletar um tipo de Emprego pelo ID
    public void deletar (Long id) {
        tipoDeEmpregoRepository.deleteById(id); // Remove do Banco de dados pelo ID
    }

}
