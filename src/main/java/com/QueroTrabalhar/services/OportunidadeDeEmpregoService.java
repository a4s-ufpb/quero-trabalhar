package com.QueroTrabalhar.services;

import com.QueroTrabalhar.domain.dtos.oportunidadeDeEmprego.OportunidadeDeEmpregoRequestDTO;
import com.QueroTrabalhar.domain.dtos.oportunidadeDeEmprego.OportunidadeDeEmpregoResponseDTO;
import com.QueroTrabalhar.domain.entity.Empresa;
import com.QueroTrabalhar.domain.entity.OportunidadeDeEmprego;
import com.QueroTrabalhar.domain.entity.PerfilRecrutador;
import com.QueroTrabalhar.domain.entity.TipoDeEmprego;
import com.QueroTrabalhar.domain.entity.localidade.Cidade;
import com.QueroTrabalhar.domain.entity.localidade.Estado;
import com.QueroTrabalhar.domain.entity.localidade.Localidade;
import com.QueroTrabalhar.domain.entity.localidade.Pais;
import com.QueroTrabalhar.domain.enums.StatusVinculoEmpresa;
import com.QueroTrabalhar.repository.CidadeRepository;
import com.QueroTrabalhar.repository.EstadoRepository;
import com.QueroTrabalhar.repository.OportunidadeDeEmpregoRepository;
import com.QueroTrabalhar.repository.PaisRepository;
import com.QueroTrabalhar.repository.TipoDeEmpregoRepository;
import com.QueroTrabalhar.services.exceptions.BusinessRuleException;
import com.QueroTrabalhar.services.exceptions.ObjectNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OportunidadeDeEmpregoService {

    private final OportunidadeDeEmpregoRepository oportunidadeDeEmpregoRepository;
    private final TipoDeEmpregoRepository tipoDeEmpregoRepository;
    private final PaisRepository paisRepository;
    private final EstadoRepository estadoRepository;
    private final CidadeRepository cidadeRepository;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public OportunidadeDeEmpregoService(
            OportunidadeDeEmpregoRepository oportunidadeDeEmpregoRepository,
            TipoDeEmpregoRepository tipoDeEmpregoRepository,
            PaisRepository paisRepository,
            EstadoRepository estadoRepository,
            CidadeRepository cidadeRepository,
            UsuarioAutenticadoService usuarioAutenticadoService
    ) {
        this.oportunidadeDeEmpregoRepository = oportunidadeDeEmpregoRepository;
        this.tipoDeEmpregoRepository = tipoDeEmpregoRepository;
        this.paisRepository = paisRepository;
        this.estadoRepository = estadoRepository;
        this.cidadeRepository = cidadeRepository;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    @Transactional(readOnly = true)
    public List<OportunidadeDeEmpregoResponseDTO> listarTodasOportunidadesDeEmprego() {
        return oportunidadeDeEmpregoRepository.findAll().stream()
                .map(OportunidadeDeEmpregoResponseDTO::daEntidade)
                .toList();
    }

    @Transactional(readOnly = true)
    public OportunidadeDeEmpregoResponseDTO buscarPorId(Long id) {
        return OportunidadeDeEmpregoResponseDTO.daEntidade(buscarEntidadePorId(id));
    }

    @Transactional
    public OportunidadeDeEmpregoResponseDTO criarOportunidadeDeEmprego(OportunidadeDeEmpregoRequestDTO dto) {
        PerfilRecrutador perfilRecrutador = obterPerfilRecrutadorAutenticado();
        TipoDeEmprego tipoDeEmprego = buscarTipoDeEmpregoValido(dto.tipoDeEmpregoId());
        Localidade localidade = montarLocalidade(dto.paisId(), dto.estadoId(), dto.cidadeId());
        Empresa empresaDaOportunidade = resolverEmpresaDaOportunidade(
                perfilRecrutador,
                dto.publicarComoEmpresa()
        );

        OportunidadeDeEmprego oportunidadeDeEmprego = new OportunidadeDeEmprego(
                dto.descricao(),
                tipoDeEmprego,
                dto.modalidade(),
                localidade,
                perfilRecrutador,
                empresaDaOportunidade
        );

        perfilRecrutador.adicionarOportunidadePostada(oportunidadeDeEmprego);

        return OportunidadeDeEmpregoResponseDTO.daEntidade(
                oportunidadeDeEmpregoRepository.save(oportunidadeDeEmprego)
        );
    }

    @Transactional
    public OportunidadeDeEmpregoResponseDTO atualizarOportunidadeDeEmprego(
            Long id,
            OportunidadeDeEmpregoRequestDTO dto
    ) {
        PerfilRecrutador perfilRecrutador = obterPerfilRecrutadorAutenticado();
        OportunidadeDeEmprego oportunidadeDeEmprego = buscarEntidadePorId(id);

        validarDonoDaOportunidade(oportunidadeDeEmprego, perfilRecrutador);

        // Nesta fase, o contexto original de publicação da oportunidade é preservado.
        oportunidadeDeEmprego.setDescricao(dto.descricao());
        oportunidadeDeEmprego.setTipoDeEmprego(buscarTipoDeEmpregoValido(dto.tipoDeEmpregoId()));
        oportunidadeDeEmprego.setModalidade(dto.modalidade());
        oportunidadeDeEmprego.setLocalizacao(montarLocalidade(dto.paisId(), dto.estadoId(), dto.cidadeId()));

        return OportunidadeDeEmpregoResponseDTO.daEntidade(
                oportunidadeDeEmpregoRepository.save(oportunidadeDeEmprego)
        );
    }

    @Transactional
    public void removerOportunidadeDeEmprego(Long id) {
        PerfilRecrutador perfilRecrutador = obterPerfilRecrutadorAutenticado();
        OportunidadeDeEmprego oportunidadeDeEmprego = buscarEntidadePorId(id);

        validarDonoDaOportunidade(oportunidadeDeEmprego, perfilRecrutador);

        oportunidadeDeEmpregoRepository.removerTodosInteressesDaVaga(id);
        oportunidadeDeEmpregoRepository.delete(oportunidadeDeEmprego);
    }

    private OportunidadeDeEmprego buscarEntidadePorId(Long id) {
        return oportunidadeDeEmpregoRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Oportunidade de emprego não encontrada. ID: " + id));
    }

    PerfilRecrutador obterPerfilRecrutadorAutenticado() {
        return usuarioAutenticadoService.obterPerfilRecrutadorAutenticado();
    }

    private TipoDeEmprego buscarTipoDeEmpregoValido(Long tipoDeEmpregoId) {
        TipoDeEmprego tipoDeEmprego = tipoDeEmpregoRepository.findById(tipoDeEmpregoId)
                .orElseThrow(() -> new ObjectNotFoundException(
                        "Tipo de emprego não encontrado. ID: " + tipoDeEmpregoId
                ));

        if (!tipoDeEmprego.isAprovado()) {
            throw new BusinessRuleException(
                    "O tipo de emprego informado ainda não foi aprovado e não pode ser usado em oportunidades."
            );
        }

        return tipoDeEmprego;
    }

    private Localidade montarLocalidade(Long paisId, Long estadoId, Long cidadeId) {
        if (cidadeId != null && estadoId == null) {
            throw new BusinessRuleException("Para informar uma cidade, o estado também deve ser informado.");
        }

        Pais pais = paisRepository.findById(paisId)
                .orElseThrow(() -> new ObjectNotFoundException("País não encontrado. ID: " + paisId));

        if (estadoId == null) {
            return new Localidade(pais);
        }

        Estado estado = estadoRepository.findById(estadoId)
                .orElseThrow(() -> new ObjectNotFoundException("Estado não encontrado. ID: " + estadoId));

        if (!estado.getPais().getId().equals(pais.getId())) {
            throw new BusinessRuleException("O estado informado não pertence ao país informado.");
        }

        if (cidadeId == null) {
            return new Localidade(pais, estado);
        }

        Cidade cidade = cidadeRepository.findById(cidadeId)
                .orElseThrow(() -> new ObjectNotFoundException("Cidade não encontrada. ID: " + cidadeId));

        if (!cidade.getEstado().getId().equals(estado.getId())) {
            throw new BusinessRuleException("A cidade informada não pertence ao estado informado.");
        }

        return new Localidade(pais, estado, cidade);
    }

    private Empresa resolverEmpresaDaOportunidade(
            PerfilRecrutador perfilRecrutador,
            Boolean publicarComoEmpresa
    ) {
        if (!Boolean.TRUE.equals(publicarComoEmpresa)) {
            return null;
        }

        if (perfilRecrutador.getEmpresaVinculada() == null) {
            throw new BusinessRuleException(
                    "Para publicar uma oportunidade em nome da empresa, o recrutador autenticado precisa possuir empresa vinculada."
            );
        }

        if (perfilRecrutador.getStatusVinculoEmpresa() != StatusVinculoEmpresa.APROVADO) {
            throw new BusinessRuleException(
                    "Para publicar uma oportunidade em nome da empresa, o vínculo com a empresa precisa estar aprovado."
            );
        }

        return perfilRecrutador.getEmpresaVinculada();
    }

    private void validarDonoDaOportunidade(
            OportunidadeDeEmprego oportunidadeDeEmprego,
            PerfilRecrutador perfilRecrutadorAutenticado
    ) {
        if (!oportunidadeDeEmprego.getPerfilRecrutador().getId().equals(perfilRecrutadorAutenticado.getId())) {
            throw new BusinessRuleException(
                    "O recrutador autenticado não pode alterar uma oportunidade que pertence a outro perfil."
            );
        }
    }
}
