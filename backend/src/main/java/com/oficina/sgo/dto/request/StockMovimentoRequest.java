package com.oficina.sgo.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record StockMovimentoRequest(
        @NotNull(message = "Quantidade is required") @Positive Integer quantidade,
        String observacoes
) {}
