package com.oficina.sgo.controller;

import com.oficina.sgo.dto.request.CreateClienteRequest;
import com.oficina.sgo.dto.response.ClienteResponse;
import com.oficina.sgo.dto.response.ViaturaResponse;
import com.oficina.sgo.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping
    public ResponseEntity<List<ClienteResponse>> findAll() {
        return ResponseEntity.ok(clienteService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.findById(id));
    }

    @GetMapping("/{id}/viaturas")
    public ResponseEntity<List<ViaturaResponse>> findViaturas(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.findViaturasByCliente(id));
    }

    @PostMapping
    public ResponseEntity<ClienteResponse> create(@Valid @RequestBody CreateClienteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> update(@PathVariable Long id, @Valid @RequestBody CreateClienteRequest request) {
        return ResponseEntity.ok(clienteService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        clienteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
