package com.oficina.sgo.service;

import com.oficina.sgo.dto.request.CreateClienteRequest;
import com.oficina.sgo.dto.response.ClienteResponse;
import com.oficina.sgo.dto.response.ViaturaResponse;
import com.oficina.sgo.exception.BusinessException;
import com.oficina.sgo.exception.ResourceNotFoundException;
import com.oficina.sgo.model.Cliente;
import com.oficina.sgo.repository.ClienteRepository;
import com.oficina.sgo.repository.ViaturaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ViaturaRepository viaturaRepository;

    public List<ClienteResponse> findAll() {
        return clienteRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public ClienteResponse findById(Long id) {
        return toResponse(clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", id)));
    }

    public List<ViaturaResponse> findViaturasByCliente(Long clienteId) {
        clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", clienteId));
        return viaturaRepository.findByClienteId(clienteId).stream()
                .map(v -> new ViaturaResponse(v.getId(), v.getMatricula(), v.getMarca(), v.getModelo(),
                        v.getAno(), v.getNumeroChassis(), v.getCombustivel(), v.getCor(),
                        v.getQuilometragem(), v.getCliente().getId(), v.getCliente().getNome()))
                .collect(Collectors.toList());
    }

    @Transactional
    public ClienteResponse create(CreateClienteRequest request) {
        if (request.nif() != null && !request.nif().isBlank() && clienteRepository.existsByNif(request.nif())) {
            throw new BusinessException("NIF already exists: " + request.nif());
        }
        Cliente cliente = Cliente.builder()
                .nome(request.nome())
                .nif(request.nif())
                .telefone(request.telefone())
                .email(request.email())
                .morada(request.morada())
                .observacoes(request.observacoes())
                .build();
        return toResponse(clienteRepository.save(cliente));
    }

    @Transactional
    public ClienteResponse update(Long id, CreateClienteRequest request) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", id));
        if (request.nif() != null && !request.nif().equals(cliente.getNif()) && clienteRepository.existsByNif(request.nif())) {
            throw new BusinessException("NIF already exists: " + request.nif());
        }
        cliente.setNome(request.nome());
        cliente.setNif(request.nif());
        cliente.setTelefone(request.telefone());
        cliente.setEmail(request.email());
        cliente.setMorada(request.morada());
        cliente.setObservacoes(request.observacoes());
        return toResponse(clienteRepository.save(cliente));
    }

    @Transactional
    public void delete(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", id));
        clienteRepository.delete(cliente);
    }

    private ClienteResponse toResponse(Cliente c) {
        return new ClienteResponse(c.getId(), c.getNome(), c.getNif(), c.getTelefone(),
                c.getEmail(), c.getMorada(), c.getObservacoes(),
                c.getViaturas() != null ? c.getViaturas().size() : 0);
    }
}
