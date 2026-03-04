package com.oficina.sgo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateViaturaRequest(
        @NotBlank(message = "Matricula is required") String matricula,
        @NotBlank(message = "Marca is required") String marca,
        @NotBlank(message = "Modelo is required") String modelo,
        Integer ano,
        String numeroChassis,
        String combustivel,
        String cor,
        Integer quilometragem,
        @NotNull(message = "Cliente ID is required") Long clienteId
) {}
