package com.oficina.sgo.controller;

import com.oficina.sgo.dto.request.CreateAgendamentoRequest;
import com.oficina.sgo.dto.response.AgendamentoResponse;
import com.oficina.sgo.service.AgendaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/agenda")
@RequiredArgsConstructor
public class AgendaController {

    private final AgendaService agendaService;

    @GetMapping
    public ResponseEntity<List<AgendamentoResponse>> findAll(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return ResponseEntity.ok(agendaService.findAll(inicio, fim));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgendamentoResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(agendaService.findById(id));
    }

    @GetMapping("/semana/{data}")
    public ResponseEntity<List<AgendamentoResponse>> findBySemana(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        return ResponseEntity.ok(agendaService.findBySemana(data));
    }

    @PostMapping
    public ResponseEntity<AgendamentoResponse> create(@Valid @RequestBody CreateAgendamentoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(agendaService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AgendamentoResponse> update(@PathVariable Long id, @Valid @RequestBody CreateAgendamentoRequest request) {
        return ResponseEntity.ok(agendaService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        agendaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
