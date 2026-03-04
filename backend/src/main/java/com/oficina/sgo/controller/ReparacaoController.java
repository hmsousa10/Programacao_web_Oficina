package com.oficina.sgo.controller;

import com.oficina.sgo.dto.request.CreateOperacaoRequest;
import com.oficina.sgo.dto.request.CreateReparacaoRequest;
import com.oficina.sgo.dto.response.OperacaoResponse;
import com.oficina.sgo.dto.response.ReparacaoResponse;
import com.oficina.sgo.service.ReparacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reparacoes")
@RequiredArgsConstructor
public class ReparacaoController {

    private final ReparacaoService reparacaoService;

    @GetMapping
    public ResponseEntity<List<ReparacaoResponse>> findAll() {
        return ResponseEntity.ok(reparacaoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReparacaoResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(reparacaoService.findById(id));
    }

    @GetMapping("/mecanico/{mecanicoId}")
    public ResponseEntity<List<ReparacaoResponse>> findByMecanico(@PathVariable Long mecanicoId) {
        return ResponseEntity.ok(reparacaoService.findByMecanico(mecanicoId));
    }

    @PostMapping
    public ResponseEntity<ReparacaoResponse> create(@Valid @RequestBody CreateReparacaoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reparacaoService.create(request));
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<ReparacaoResponse> updateEstado(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(reparacaoService.updateEstado(id, body.get("estado")));
    }

    @PostMapping("/{id}/operacoes")
    public ResponseEntity<OperacaoResponse> addOperacao(@PathVariable Long id, @Valid @RequestBody CreateOperacaoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reparacaoService.addOperacao(id, request));
    }

    @PutMapping("/{id}/operacoes/{opId}")
    public ResponseEntity<OperacaoResponse> updateOperacao(@PathVariable Long id, @PathVariable Long opId,
            @Valid @RequestBody CreateOperacaoRequest request) {
        return ResponseEntity.ok(reparacaoService.updateOperacao(id, opId, request));
    }
}
