package com.oficina.sgo.service;

import com.oficina.sgo.dao.AgendamentoDao;
import com.oficina.sgo.dao.ClienteDao;
import com.oficina.sgo.dao.OperacaoReparacaoDao;
import com.oficina.sgo.dao.ReparacaoDao;
import com.oficina.sgo.dao.UserDao;
import com.oficina.sgo.dao.ViaturaDao;
import com.oficina.sgo.dto.request.CreateOperacaoRequest;
import com.oficina.sgo.dto.request.CreateReparacaoRequest;
import com.oficina.sgo.dto.response.OperacaoResponse;
import com.oficina.sgo.dto.response.ReparacaoResponse;
import com.oficina.sgo.exception.BusinessException;
import com.oficina.sgo.exception.CapacidadeOficinaException;
import com.oficina.sgo.exception.ResourceNotFoundException;
import com.oficina.sgo.model.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ReparacaoService {

    private static final int MAX_REPARACOES_ATIVAS = 8;

    private final EntityManagerFactory emf;
    private final ReparacaoDao reparacaoDao;
    private final OperacaoReparacaoDao operacaoDao;
    private final ClienteDao clienteDao;
    private final ViaturaDao viaturaDao;
    private final UserDao userDao;
    private final AgendamentoDao agendamentoDao;

    public ReparacaoService(EntityManagerFactory emf) {
        this.emf = emf;
        this.reparacaoDao = new ReparacaoDao();
        this.operacaoDao = new OperacaoReparacaoDao();
        this.clienteDao = new ClienteDao();
        this.viaturaDao = new ViaturaDao();
        this.userDao = new UserDao();
        this.agendamentoDao = new AgendamentoDao();
    }

    public List<ReparacaoResponse> findAll() {
        try (EntityManager em = emf.createEntityManager()) {
            return reparacaoDao.findAll(em).stream().map(this::toResponse).collect(Collectors.toList());
        }
    }

    public ReparacaoResponse findById(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            return toResponse(reparacaoDao.findById(em, id)
                    .orElseThrow(() -> new ResourceNotFoundException("Reparacao", id)));
        }
    }

    public List<ReparacaoResponse> findByMecanico(Long mecanicoId) {
        try (EntityManager em = emf.createEntityManager()) {
            return reparacaoDao.findByMecanicoId(em, mecanicoId).stream()
                    .map(this::toResponse).collect(Collectors.toList());
        }
    }

    public ReparacaoResponse create(CreateReparacaoRequest request) {
        return inTransaction(em -> {
            Cliente cliente = clienteDao.findById(em, request.clienteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente", request.clienteId()));
            Viatura viatura = viaturaDao.findById(em, request.viaturaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Viatura", request.viaturaId()));
            User mecanico = null;
            if (request.mecanicoId() != null) {
                mecanico = userDao.findById(em, request.mecanicoId())
                        .orElseThrow(() -> new ResourceNotFoundException("User", request.mecanicoId()));
            }
            Agendamento agendamento = null;
            if (request.agendamentoId() != null) {
                agendamento = agendamentoDao.findById(em, request.agendamentoId())
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
            return toResponse(reparacaoDao.save(em, reparacao));
        });
    }

    public ReparacaoResponse updateEstado(Long id, String estado) {
        return inTransaction(em -> {
            Reparacao reparacao = reparacaoDao.findById(em, id)
                    .orElseThrow(() -> new ResourceNotFoundException("Reparacao", id));
            Reparacao.EstadoReparacao novoEstado;
            try {
                novoEstado = Reparacao.EstadoReparacao.valueOf(estado.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BusinessException("Invalid estado: " + estado);
            }
            if (novoEstado == Reparacao.EstadoReparacao.EM_EXECUCAO) {
                long ativas = reparacaoDao.countAtivas(em);
                if (ativas >= MAX_REPARACOES_ATIVAS) {
                    throw new CapacidadeOficinaException(
                            "Oficina at maximum capacity (" + MAX_REPARACOES_ATIVAS + " active reparacoes)");
                }
                reparacao.setDataInicio(LocalDateTime.now());
            } else if (novoEstado == Reparacao.EstadoReparacao.CONCLUIDA) {
                reparacao.setDataFim(LocalDateTime.now());
            }
            reparacao.setEstado(novoEstado);
            return toResponse(reparacaoDao.save(em, reparacao));
        });
    }

    public OperacaoResponse addOperacao(Long reparacaoId, CreateOperacaoRequest request) {
        return inTransaction(em -> {
            Reparacao reparacao = reparacaoDao.findById(em, reparacaoId)
                    .orElseThrow(() -> new ResourceNotFoundException("Reparacao", reparacaoId));
            OperacaoReparacao operacao = OperacaoReparacao.builder()
                    .reparacao(reparacao)
                    .descricao(request.descricao())
                    .tempoEstimadoMinutos(request.tempoEstimadoMinutos())
                    .observacoes(request.observacoes())
                    .estado(OperacaoReparacao.EstadoOperacao.PENDENTE)
                    .build();
            return toOperacaoResponse(operacaoDao.save(em, operacao));
        });
    }

    public OperacaoResponse updateOperacao(Long reparacaoId, Long opId, CreateOperacaoRequest request) {
        return inTransaction(em -> {
            reparacaoDao.findById(em, reparacaoId)
                    .orElseThrow(() -> new ResourceNotFoundException("Reparacao", reparacaoId));
            OperacaoReparacao operacao = operacaoDao.findById(em, opId)
                    .orElseThrow(() -> new ResourceNotFoundException("Operacao", opId));
            operacao.setDescricao(request.descricao());
            operacao.setTempoEstimadoMinutos(request.tempoEstimadoMinutos());
            operacao.setObservacoes(request.observacoes());
            return toOperacaoResponse(operacaoDao.save(em, operacao));
        });
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

    private <T> T inTransaction(Function<EntityManager, T> action) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            T result = action.apply(em);
            tx.commit();
            return result;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
