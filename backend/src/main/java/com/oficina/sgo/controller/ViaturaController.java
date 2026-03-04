package com.oficina.sgo.controller;

import com.oficina.sgo.dto.request.CreateViaturaRequest;
import com.oficina.sgo.dto.response.ViaturaResponse;
import com.oficina.sgo.service.ViaturaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/viaturas")
@RequiredArgsConstructor
public class ViaturaController {

    private final ViaturaService viaturaService;

    @GetMapping
    public ResponseEntity<List<ViaturaResponse>> findAll() {
        return ResponseEntity.ok(viaturaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ViaturaResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(viaturaService.findById(id));
    }

    @GetMapping("/matricula/{matricula}")
    public ResponseEntity<ViaturaResponse> findByMatricula(@PathVariable String matricula) {
        return ResponseEntity.ok(viaturaService.findByMatricula(matricula));
    }

    @PostMapping
    public ResponseEntity<ViaturaResponse> create(@Valid @RequestBody CreateViaturaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(viaturaService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ViaturaResponse> update(@PathVariable Long id, @Valid @RequestBody CreateViaturaRequest request) {
        return ResponseEntity.ok(viaturaService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        viaturaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
