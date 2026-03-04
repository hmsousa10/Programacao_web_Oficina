package com.oficina.sgo.service;

import com.oficina.sgo.dto.request.CreateViaturaRequest;
import com.oficina.sgo.dto.response.ViaturaResponse;
import com.oficina.sgo.exception.BusinessException;
import com.oficina.sgo.exception.ResourceNotFoundException;
import com.oficina.sgo.model.Cliente;
import com.oficina.sgo.model.Viatura;
import com.oficina.sgo.repository.ClienteRepository;
import com.oficina.sgo.repository.ViaturaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ViaturaService {

    private final ViaturaRepository viaturaRepository;
    private final ClienteRepository clienteRepository;

    public List<ViaturaResponse> findAll() {
        return viaturaRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public ViaturaResponse findById(Long id) {
        return toResponse(viaturaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Viatura", id)));
    }

    public ViaturaResponse findByMatricula(String matricula) {
        return toResponse(viaturaRepository.findByMatricula(matricula)
                .orElseThrow(() -> new ResourceNotFoundException("Viatura not found with matricula: " + matricula)));
    }

    @Transactional
    public ViaturaResponse create(CreateViaturaRequest request) {
        if (viaturaRepository.existsByMatricula(request.matricula())) {
            throw new BusinessException("Matricula already exists: " + request.matricula());
        }
        Cliente cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", request.clienteId()));
        Viatura viatura = Viatura.builder()
                .matricula(request.matricula())
                .marca(request.marca())
                .modelo(request.modelo())
                .ano(request.ano())
                .numeroChassis(request.numeroChassis())
                .combustivel(request.combustivel())
                .cor(request.cor())
                .quilometragem(request.quilometragem())
                .cliente(cliente)
                .build();
        return toResponse(viaturaRepository.save(viatura));
    }

    @Transactional
    public ViaturaResponse update(Long id, CreateViaturaRequest request) {
        Viatura viatura = viaturaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Viatura", id));
        if (!viatura.getMatricula().equals(request.matricula()) && viaturaRepository.existsByMatricula(request.matricula())) {
            throw new BusinessException("Matricula already exists: " + request.matricula());
        }
        Cliente cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", request.clienteId()));
        viatura.setMatricula(request.matricula());
        viatura.setMarca(request.marca());
        viatura.setModelo(request.modelo());
        viatura.setAno(request.ano());
        viatura.setNumeroChassis(request.numeroChassis());
        viatura.setCombustivel(request.combustivel());
        viatura.setCor(request.cor());
        viatura.setQuilometragem(request.quilometragem());
        viatura.setCliente(cliente);
        return toResponse(viaturaRepository.save(viatura));
    }

    @Transactional
    public void delete(Long id) {
        Viatura viatura = viaturaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Viatura", id));
        viaturaRepository.delete(viatura);
    }

    private ViaturaResponse toResponse(Viatura v) {
        return new ViaturaResponse(v.getId(), v.getMatricula(), v.getMarca(), v.getModelo(),
                v.getAno(), v.getNumeroChassis(), v.getCombustivel(), v.getCor(),
                v.getQuilometragem(), v.getCliente().getId(), v.getCliente().getNome());
    }
}
