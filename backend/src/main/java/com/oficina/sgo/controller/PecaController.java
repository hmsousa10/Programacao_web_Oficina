package com.oficina.sgo.controller;

import com.oficina.sgo.dto.request.CreatePecaRequest;
import com.oficina.sgo.dto.request.RequisitarPecaRequest;
import com.oficina.sgo.dto.request.StockMovimentoRequest;
import com.oficina.sgo.dto.response.PecaResponse;
import com.oficina.sgo.service.PecaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pecas")
@RequiredArgsConstructor
public class PecaController {

    private final PecaService pecaService;

    @GetMapping
    public ResponseEntity<List<PecaResponse>> findAll() {
        return ResponseEntity.ok(pecaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PecaResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(pecaService.findById(id));
    }

    @GetMapping("/alertas-stock")
    public ResponseEntity<List<PecaResponse>> findAlertasStock() {
        return ResponseEntity.ok(pecaService.findAlertasStock());
    }

    @PostMapping
    public ResponseEntity<PecaResponse> create(@Valid @RequestBody CreatePecaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pecaService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PecaResponse> update(@PathVariable Long id, @Valid @RequestBody CreatePecaRequest request) {
        return ResponseEntity.ok(pecaService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        pecaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/entrada-stock")
    public ResponseEntity<PecaResponse> entradaStock(@PathVariable Long id, @Valid @RequestBody StockMovimentoRequest request) {
        return ResponseEntity.ok(pecaService.entradaStock(id, request));
    }

    @PostMapping("/{id}/saida-stock")
    public ResponseEntity<PecaResponse> saidaStock(@PathVariable Long id, @Valid @RequestBody StockMovimentoRequest request) {
        return ResponseEntity.ok(pecaService.saidaStock(id, request));
    }

    @PostMapping("/requisitar")
    public ResponseEntity<PecaResponse> requisitar(@Valid @RequestBody RequisitarPecaRequest request) {
        return ResponseEntity.ok(pecaService.requisitarPeca(request));
    }
}
