package com.QueroTrabalhar.services;

import com.QueroTrabalhar.domain.entity.*;
import com.QueroTrabalhar.domain.enums.Modalidade;
import com.QueroTrabalhar.domain.enums.Role;
import com.QueroTrabalhar.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

@Service
public class SemeadorBd {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioRecrutadorRepository usuarioRecrutadorRepository;
    private final TipoDeEmpregoRepository tipoDeEmpregoRepository;
    private final OportunidadeDeEmpregoRepository oportunidadeDeEmpregoRepository;
    private final InteresseEmOportunidadesRepository interesseEmOportunidadesRepository;
    private final InteresseEmEmpregoRepository interesseEmEmpregoRepository;
    private final IndicacoesRepository indicacoesRepository;
    private final ExperienciaProfissionalRepository experienciaProfissionalRepository;
    @Autowired
    private BCryptPasswordEncoder encoder;

    public SemeadorBd(UsuarioRepository usuarioRepository,
                      UsuarioRecrutadorRepository usuarioRecrutadorRepository,
                      TipoDeEmpregoRepository tipoDeEmpregoRepository,
                      OportunidadeDeEmpregoRepository oportunidadeDeEmpregoRepository,
                      InteresseEmOportunidadesRepository interesseEmOportunidadesRepository,
                      InteresseEmEmpregoRepository interesseEmEmpregoRepository,
                      IndicacoesRepository indicacoesRepository,
                      ExperienciaProfissionalRepository experienciaProfissionalRepository) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioRecrutadorRepository = usuarioRecrutadorRepository;
        this.tipoDeEmpregoRepository = tipoDeEmpregoRepository;
        this.oportunidadeDeEmpregoRepository = oportunidadeDeEmpregoRepository;
        this.interesseEmOportunidadesRepository = interesseEmOportunidadesRepository;
        this.interesseEmEmpregoRepository = interesseEmEmpregoRepository;
        this.indicacoesRepository = indicacoesRepository;
        this.experienciaProfissionalRepository = experienciaProfissionalRepository;
    }

    public void populardBd(){
        limparBd();

        // 1. Criar Categorias de Emprego (TipoDeEmprego)
        TipoDeEmprego catTI = new TipoDeEmprego(null, "Tecnologia", "Desenvolvimento e Suporte");
        TipoDeEmprego catManutencao = new TipoDeEmprego(null, "Manutenção", "Reparos gerais e elétrica");
        TipoDeEmprego catAdmin = new TipoDeEmprego(null, "Administrativo", "Auxiliar e secretariado");

        tipoDeEmpregoRepository.saveAll(Arrays.asList(catTI, catManutencao, catAdmin));

        // 2. Criar Usuários Comuns (Seus dados fictícios adaptados)
        // Nota: CPF mantido sem encoder para validação e busca
        Usuario u1 = new Usuario(null, "63069141021", "Dan Pink", "83999990001", "dan@gmail.com", encoder.encode("^&OR#4fhaa5gXYJG"), Role.USER);
        Usuario u2 = new Usuario(null, "75772202057", "Emma Black", "83999990002", "emma@gmail.com", encoder.encode("C:~`F_|4t({__FG:"), Role.USER);
        Usuario u3 = new Usuario(null, "98368074037", "Finn Grey", "83999990003", "finn@gmail.com", encoder.encode("1j]g_;.v944+N(g|"), Role.USER);

        // Adicionar Experiências (Opcional, mas bom para teste)
        ExperienciaProfissional exp1 = new ExperienciaProfissional(null, u1, catTI, "Desenvolvedor Junior Java", LocalDate.of(2022, 1, 10), LocalDate.of(2023, 5, 20));
        u1.getExperienciaProfissionais().add(exp1);

        usuarioRepository.saveAll(Arrays.asList(u1, u2, u3));

        // 3. Criar Recrutadores (Empresas)
        UsuarioRecrutador rec1 = new UsuarioRecrutador(null, "Tech Solutions RH", "rh@techsolutions.com", "83988887777", "Tech Soluções Ltda", new ArrayList<>());
        UsuarioRecrutador rec2 = new UsuarioRecrutador(null, "Construções Rápidas", "contato@construcoes.com", "83977776666", "Empreiteira Silva", new ArrayList<>());

        usuarioRecrutadorRepository.saveAll(Arrays.asList(rec1, rec2));

        // 4. Criar Oportunidades de Emprego (Vagas)
        OportunidadeDeEmprego op1 = new OportunidadeDeEmprego(null, "Vaga para Dev Spring Boot", catTI, Modalidade.REMOTO, "João Pessoa", "PB", rec1);
        OportunidadeDeEmprego op2 = new OportunidadeDeEmprego(null, "Eletricista Predial", catManutencao, Modalidade.PRESENCIAL, "Campina Grande", "PB", rec2);
        OportunidadeDeEmprego op3 = new OportunidadeDeEmprego(null, "Auxiliar Administrativo", catAdmin, Modalidade.HIBRIDO, "Cabedelo", "PB", rec1);

        oportunidadeDeEmpregoRepository.saveAll(Arrays.asList(op1, op2, op3));

        // Dan gosta de TI
        u1.getInteresseEmOportunidades().getOportunidades().add(op1);

        // Emma gosta de TI e Admin
        u2.getInteresseEmOportunidades().getOportunidades().addAll(Arrays.asList(op1, op3));

        // Finn gosta de Manutenção
        u3.getInteresseEmOportunidades().getOportunidades().add(op2);

        usuarioRepository.saveAll(Arrays.asList(u1, u2, u3));

        System.out.println("--------------------------------------");
        System.out.println("BANCO DE DADOS SEMEADO COM SUCESSO");
        System.out.println("--------------------------------------");

    }

    private void limparBd() {
        this.usuarioRepository.deleteAll();
        this.usuarioRecrutadorRepository.deleteAll();
        this.tipoDeEmpregoRepository.deleteAll();
        this.oportunidadeDeEmpregoRepository.deleteAll();
        this.interesseEmOportunidadesRepository.deleteAll();
        this.interesseEmEmpregoRepository.deleteAll();
        this.indicacoesRepository.deleteAll();
        this.experienciaProfissionalRepository.deleteAll();
    }


}
