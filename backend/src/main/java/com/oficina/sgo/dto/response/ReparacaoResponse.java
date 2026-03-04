package com.oficina.sgo.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ReparacaoResponse(
        Long id,
        Long agendamentoId,
        Long viaturaId,
        String viaturaMatricula,
        Long clienteId,
        String clienteNome,
        Long mecanicoId,
        String mecanicoNome,
        LocalDateTime dataInicio,
        LocalDateTime dataFim,
        String estado,
        String descricao,
        Integer tempoTotalMinutos,
        BigDecimal valorTotal,
        List<OperacaoResponse> operacoes
) {}
