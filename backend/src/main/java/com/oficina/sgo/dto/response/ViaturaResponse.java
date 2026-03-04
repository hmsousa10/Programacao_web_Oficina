package com.oficina.sgo.dto.response;

public record ViaturaResponse(
        Long id,
        String matricula,
        String marca,
        String modelo,
        Integer ano,
        String numeroChassis,
        String combustivel,
        String cor,
        Integer quilometragem,
        Long clienteId,
        String clienteNome
) {}
