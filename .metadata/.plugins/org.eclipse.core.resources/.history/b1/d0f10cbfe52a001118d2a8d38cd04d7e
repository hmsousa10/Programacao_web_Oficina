package com.oficina.sgo.dto.request;

import jakarta.validation.constraints.NotNull;

public record CreateReparacaoRequest(
        Long agendamentoId,
        @NotNull(message = "Viatura ID is required") Long viaturaId,
        @NotNull(message = "Cliente ID is required") Long clienteId,
        Long mecanicoId,
        String descricao
) {}
