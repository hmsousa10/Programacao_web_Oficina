package com.oficina.sgo.service;

import com.oficina.sgo.dto.request.CreateAgendamentoRequest;
import com.oficina.sgo.dto.response.AgendamentoResponse;
import com.oficina.sgo.exception.CapacidadeAgendaException;
import com.oficina.sgo.exception.ResourceNotFoundException;
import com.oficina.sgo.model.Agendamento;
import com.oficina.sgo.model.Cliente;
import com.oficina.sgo.model.User;
import com.oficina.sgo.model.Viatura;
import com.oficina.sgo.repository.AgendamentoRepository;
import com.oficina.sgo.repository.ClienteRepository;
import com.oficina.sgo.repository.UserRepository;
import com.oficina.sgo.repository.ViaturaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgendaService {

    private static final int MAX_AGENDAMENTOS_POR_SLOT = 3;

    private final AgendamentoRepository agendamentoRepository;
    private final ClienteRepository clienteRepository;
    private final ViaturaRepository viaturaRepository;
    private final UserRepository userRepository;

    public List<AgendamentoResponse> findAll(LocalDateTime inicio, LocalDateTime fim) {
        List<Agendamento> agendamentos;
        if (inicio != null && fim != null) {
            agendamentos = agendamentoRepository.findByPeriod(inicio, fim);
        } else {
            agendamentos = agendamentoRepository.findAll();
        }
        return agendamentos.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public AgendamentoResponse findById(Long id) {
        return toResponse(agendamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento", id)));
    }

    public List<AgendamentoResponse> findBySemana(LocalDate data) {
        LocalDateTime inicio = data.atStartOfDay();
        LocalDateTime fim = data.plusDays(7).atStartOfDay();
        return agendamentoRepository.findBySemana(inicio, fim).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public AgendamentoResponse create(CreateAgendamentoRequest request) {
        long count = agendamentoRepository.countBySlot(request.dataHoraInicio(), request.dataHoraFim());
        if (count >= MAX_AGENDAMENTOS_POR_SLOT) {
            throw new CapacidadeAgendaException(
                    "Slot already has " + count + " agendamentos. Maximum is " + MAX_AGENDAMENTOS_POR_SLOT);
        }
        Cliente cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", request.clienteId()));
        Viatura viatura = viaturaRepository.findById(request.viaturaId())
                .orElseThrow(() -> new ResourceNotFoundException("Viatura", request.viaturaId()));
        User mecanico = null;
        if (request.mecanicoId() != null) {
            mecanico = userRepository.findById(request.mecanicoId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", request.mecanicoId()));
        }
        Agendamento agendamento = Agendamento.builder()
                .dataHoraInicio(request.dataHoraInicio())
                .dataHoraFim(request.dataHoraFim())
                .cliente(cliente)
                .viatura(viatura)
                .mecanico(mecanico)
                .tipoServico(request.tipoServico())
                .observacoes(request.observacoes())
                .estado(Agendamento.EstadoAgendamento.PENDENTE)
                .build();
        return toResponse(agendamentoRepository.save(agendamento));
    }

    @Transactional
    public AgendamentoResponse update(Long id, CreateAgendamentoRequest request) {
        Agendamento agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento", id));
        boolean slotChanged = !agendamento.getDataHoraInicio().equals(request.dataHoraInicio())
                || !agendamento.getDataHoraFim().equals(request.dataHoraFim());
        if (slotChanged) {
            long count = agendamentoRepository.countBySlotExcluding(request.dataHoraInicio(), request.dataHoraFim(), id);
            if (count >= MAX_AGENDAMENTOS_POR_SLOT) {
                throw new CapacidadeAgendaException(
                        "Slot already has " + count + " agendamentos. Maximum is " + MAX_AGENDAMENTOS_POR_SLOT);
            }
        }
        Cliente cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", request.clienteId()));
        Viatura viatura = viaturaRepository.findById(request.viaturaId())
                .orElseThrow(() -> new ResourceNotFoundException("Viatura", request.viaturaId()));
        User mecanico = null;
        if (request.mecanicoId() != null) {
            mecanico = userRepository.findById(request.mecanicoId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", request.mecanicoId()));
        }
        agendamento.setDataHoraInicio(request.dataHoraInicio());
        agendamento.setDataHoraFim(request.dataHoraFim());
        agendamento.setCliente(cliente);
        agendamento.setViatura(viatura);
        agendamento.setMecanico(mecanico);
        agendamento.setTipoServico(request.tipoServico());
        agendamento.setObservacoes(request.observacoes());
        return toResponse(agendamentoRepository.save(agendamento));
    }

    @Transactional
    public void delete(Long id) {
        Agendamento agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento", id));
        agendamento.setEstado(Agendamento.EstadoAgendamento.CANCELADO);
        agendamentoRepository.save(agendamento);
    }

    private AgendamentoResponse toResponse(Agendamento a) {
        return new AgendamentoResponse(
                a.getId(), a.getDataHoraInicio(), a.getDataHoraFim(),
                a.getCliente().getId(), a.getCliente().getNome(),
                a.getViatura().getId(), a.getViatura().getMatricula(),
                a.getMecanico() != null ? a.getMecanico().getId() : null,
                a.getMecanico() != null ? a.getMecanico().getName() : null,
                a.getTipoServico(), a.getEstado().name(), a.getObservacoes()
        );
    }
}
