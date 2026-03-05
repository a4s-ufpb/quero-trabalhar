package com.QueroTrabalhar.services.bd;

import com.QueroTrabalhar.domain.entity.*;
import com.QueroTrabalhar.domain.enums.*;
import com.QueroTrabalhar.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class SemeadorBd {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private TipoDeEmpregoRepository tipoDeEmpregoRepository;
    @Autowired private OportunidadeDeEmpregoRepository oportunidadeRepository;
    @Autowired private IndicacoesRepository indicacoesRepository;
    @Autowired private BCryptPasswordEncoder encoder;

    @Transactional
    public void popularBd() {

        limparBd();

        // =========================================================
        // ONDA 1 - TIPOS DE EMPREGO
        // =========================================================

        TipoDeEmprego ti = new TipoDeEmprego("Tecnologia", "Desenvolvimento e Infraestrutura");
        TipoDeEmprego design = new TipoDeEmprego("Design", "UX/UI e Produto");
        TipoDeEmprego manutencao = new TipoDeEmprego("Manutenção", "Serviços técnicos");

        tipoDeEmpregoRepository.saveAll(List.of(ti, design, manutencao));

        // =========================================================
        // ONDA 2 - USUÁRIOS
        // =========================================================

        Usuario admin = new Usuario("27128833064", "Admin Master", "83999990000",
                "admin@qt.com", encoder.encode("123"));
        admin.addProfile(Role.ADMIN);

        Usuario candidato1 = new Usuario("63069141021", "Dan Pink", "83999990001",
                "dan@gmail.com", encoder.encode("123"));

        Usuario candidato2 = new Usuario("75772202057", "Emma Black", "83999990002",
                "emma@gmail.com", encoder.encode("123"));

        Usuario recrutadorUser = new Usuario("81004206020", "Bob Red", "83988887777",
                "rh@tech.com", encoder.encode("123"));

        // =========================================================
        // ONDA 3 - PERFIS
        // =========================================================

        // Candidatos
        PerfilCandidato perfilDan = new PerfilCandidato(candidato1);
        PerfilCandidato perfilEmma = new PerfilCandidato(candidato2);

        candidato1.adicionarPerfilCandidato(perfilDan);
        candidato2.adicionarPerfilCandidato(perfilEmma);

        // Recrutador
        PerfilRecrutador perfilRH = new PerfilRecrutador(recrutadorUser, "Tech Solutions");
        recrutadorUser.adicionarPerfilRecrutador(perfilRH);

        usuarioRepository.saveAll(List.of(admin, candidato1, candidato2, recrutadorUser));
        usuarioRepository.flush(); // força geração de IDs

        // =========================================================
        // ONDA 4 - EXPERIÊNCIAS PROFISSIONAIS
        // =========================================================

        ExperienciaProfissional exp1 = new ExperienciaProfissional(
                perfilDan,
                ti,
                "Desenvolvedor Java Backend",
                LocalDate.of(2022, 1, 10),
                LocalDate.of(2024, 1, 10)
        );

        perfilDan.adicionarExperiencia(exp1);

        // =========================================================
        // ONDA 5 - INTERESSE EM EMPREGO
        // =========================================================

        InteresseEmEmprego interesseDan = new InteresseEmEmprego(perfilDan, true);
        interesseDan.adicionarTipoInteresse(ti);
        interesseDan.adicionarTipoInteresse(design);

        interesseDan.adicionarLocal(
                new Localizacao("João Pessoa", Estado.PB, Pais.BR)
        );

        interesseDan.adicionarLocal(new Localizacao(Pais.US));

        perfilDan.definirInteresse(interesseDan);

        // =========================================================
        // ONDA 6 - OPORTUNIDADES
        // =========================================================

        Localizacao loc = new Localizacao("João Pessoa", Estado.PB, Pais.BR);

        OportunidadeDeEmprego vaga1 = new OportunidadeDeEmprego(
                "Dev Spring Boot",
                ti,
                Modalidade.REMOTO,
                loc,
                perfilRH
        );

        OportunidadeDeEmprego vaga2 = new OportunidadeDeEmprego(
                "UX Designer Pleno",
                design,
                Modalidade.HIBRIDO,
                loc,
                perfilRH
        );

        perfilRH.adicionarOportunidadePostada(vaga1);
        perfilRH.adicionarOportunidadePostada(vaga2);

        oportunidadeRepository.saveAll(List.of(vaga1, vaga2));

        // =========================================================
        // ONDA 7 - INTERESSE EM VAGAS (ManyToMany)
        // =========================================================

        perfilDan.demonstrarInteresse(vaga1);
        perfilEmma.demonstrarInteresse(vaga2);

        // =========================================================
        // ONDA 8 - INDICAÇÕES
        // =========================================================

        Indicacoes indicacao = new Indicacoes(
                candidato2,
                candidato1,
                "Excelente profissional backend!"
        );

        candidato2.adicionarIndicacaoDada(indicacao);
        candidato1.adicionarIndicacaoRecebida(indicacao);

        usuarioRepository.saveAll(List.of(candidato1, candidato2));

        System.out.println(">>> BANCO POPULADO COM SUCESSO <<<");
    }

    private void limparBd() {
        indicacoesRepository.deleteAllInBatch();
        oportunidadeRepository.deleteAllInBatch();
        usuarioRepository.deleteAllInBatch();
        tipoDeEmpregoRepository.deleteAllInBatch();
    }
}