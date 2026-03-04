package com.oficina.sgo.service;

import com.oficina.sgo.dto.request.CreateOperacaoRequest;
import com.oficina.sgo.dto.request.CreateReparacaoRequest;
import com.oficina.sgo.dto.response.OperacaoResponse;
import com.oficina.sgo.dto.response.ReparacaoResponse;
import com.oficina.sgo.exception.BusinessException;
import com.oficina.sgo.exception.CapacidadeOficinaException;
import com.oficina.sgo.exception.ResourceNotFoundException;
import com.oficina.sgo.model.*;
import com.oficina.sgo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReparacaoService {

    private static final int MAX_REPARACOES_ATIVAS = 8;

    private final ReparacaoRepository reparacaoRepository;
    private final OperacaoReparacaoRepository operacaoRepository;
    private final ClienteRepository clienteRepository;
    private final ViaturaRepository viaturaRepository;
    private final UserRepository userRepository;
    private final AgendamentoRepository agendamentoRepository;

    public List<ReparacaoResponse> findAll() {
        return reparacaoRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public ReparacaoResponse findById(Long id) {
        return toResponse(reparacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reparacao", id)));
    }

    public List<ReparacaoResponse> findByMecanico(Long mecanicoId) {
        return reparacaoRepository.findByMecanicoId(mecanicoId).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public ReparacaoResponse create(CreateReparacaoRequest request) {
        Cliente cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", request.clienteId()));
        Viatura viatura = viaturaRepository.findById(request.viaturaId())
                .orElseThrow(() -> new ResourceNotFoundException("Viatura", request.viaturaId()));
        User mecanico = null;
        if (request.mecanicoId() != null) {
            mecanico = userRepository.findById(request.mecanicoId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", request.mecanicoId()));
        }
        Agendamento agendamento = null;
        if (request.agendamentoId() != null) {
            agendamento = agendamentoRepository.findById(request.agendamentoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Agendamento", request.agendamentoId()));
        }
        Reparacao reparacao = Reparacao.builder()
                .agendamento(agendamento)
                .viatura(viatura)
                .cliente(cliente)
                .mecanico(mecanico)
                .descricao(request.descricao())
                .estado(Reparacao.EstadoReparacao.PENDENTE)
                .build();
        return toResponse(reparacaoRepository.save(reparacao));
    }

    @Transactional
    public ReparacaoResponse updateEstado(Long id, String estado) {
        Reparacao reparacao = reparacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reparacao", id));
        Reparacao.EstadoReparacao novoEstado;
        try {
            novoEstado = Reparacao.EstadoReparacao.valueOf(estado.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Invalid estado: " + estado);
        }
        if (novoEstado == Reparacao.EstadoReparacao.EM_EXECUCAO) {
            long ativas = reparacaoRepository.countAtivas();
            if (ativas >= MAX_REPARACOES_ATIVAS) {
                throw new CapacidadeOficinaException(
                        "Oficina at maximum capacity (" + MAX_REPARACOES_ATIVAS + " active reparacoes)");
            }
            reparacao.setDataInicio(LocalDateTime.now());
        } else if (novoEstado == Reparacao.EstadoReparacao.CONCLUIDA) {
            reparacao.setDataFim(LocalDateTime.now());
        }
        reparacao.setEstado(novoEstado);
        return toResponse(reparacaoRepository.save(reparacao));
    }

    @Transactional
    public OperacaoResponse addOperacao(Long reparacaoId, CreateOperacaoRequest request) {
        Reparacao reparacao = reparacaoRepository.findById(reparacaoId)
                .orElseThrow(() -> new ResourceNotFoundException("Reparacao", reparacaoId));
        OperacaoReparacao operacao = OperacaoReparacao.builder()
                .reparacao(reparacao)
                .descricao(request.descricao())
                .tempoEstimadoMinutos(request.tempoEstimadoMinutos())
                .observacoes(request.observacoes())
                .estado(OperacaoReparacao.EstadoOperacao.PENDENTE)
                .build();
        return toOperacaoResponse(operacaoRepository.save(operacao));
    }

    @Transactional
    public OperacaoResponse updateOperacao(Long reparacaoId, Long opId, CreateOperacaoRequest request) {
        reparacaoRepository.findById(reparacaoId)
                .orElseThrow(() -> new ResourceNotFoundException("Reparacao", reparacaoId));
        OperacaoReparacao operacao = operacaoRepository.findById(opId)
                .orElseThrow(() -> new ResourceNotFoundException("Operacao", opId));
        operacao.setDescricao(request.descricao());
        operacao.setTempoEstimadoMinutos(request.tempoEstimadoMinutos());
        operacao.setObservacoes(request.observacoes());
        return toOperacaoResponse(operacaoRepository.save(operacao));
    }

    private ReparacaoResponse toResponse(Reparacao r) {
        List<OperacaoResponse> operacoes = r.getOperacoes() != null
                ? r.getOperacoes().stream().map(this::toOperacaoResponse).collect(Collectors.toList())
                : List.of();
        return new ReparacaoResponse(
                r.getId(),
                r.getAgendamento() != null ? r.getAgendamento().getId() : null,
                r.getViatura().getId(), r.getViatura().getMatricula(),
                r.getCliente().getId(), r.getCliente().getNome(),
                r.getMecanico() != null ? r.getMecanico().getId() : null,
                r.getMecanico() != null ? r.getMecanico().getName() : null,
                r.getDataInicio(), r.getDataFim(), r.getEstado().name(),
                r.getDescricao(), r.getTempoTotalMinutos(), r.getValorTotal(), operacoes
        );
    }

    private OperacaoResponse toOperacaoResponse(OperacaoReparacao o) {
        return new OperacaoResponse(o.getId(), o.getReparacao().getId(), o.getDescricao(),
                o.getTempoEstimadoMinutos(), o.getTempoRealMinutos(), o.getEstado().name(),
                o.getDataInicio(), o.getDataFim(), o.getObservacoes());
    }
}
