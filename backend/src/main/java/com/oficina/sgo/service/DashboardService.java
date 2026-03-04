package com.oficina.sgo.service;

import com.oficina.sgo.dto.response.DashboardResponse;
import com.oficina.sgo.model.Reparacao;
import com.oficina.sgo.repository.AgendamentoRepository;
import com.oficina.sgo.repository.PecaRepository;
import com.oficina.sgo.repository.ReparacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final int CAPACIDADE_OFICINA = 8;

    private final ReparacaoRepository reparacaoRepository;
    private final AgendamentoRepository agendamentoRepository;
    private final PecaRepository pecaRepository;

    public DashboardResponse getKpis() {
        LocalDate today = LocalDate.now();
        LocalDateTime inicioHoje = today.atStartOfDay();
        LocalDateTime fimHoje = today.plusDays(1).atStartOfDay();

        LocalDate inicioSemanaDate = today.with(WeekFields.ISO.dayOfWeek(), 1);
        LocalDateTime inicioSemana = inicioSemanaDate.atStartOfDay();
        LocalDateTime fimSemana = inicioSemanaDate.plusDays(7).atStartOfDay();

        LocalDateTime inicioMes = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime fimMes = today.withDayOfMonth(1).plusMonths(1).atStartOfDay();

        List<Reparacao> concluidasHoje = reparacaoRepository.findConcluidasNoPeriodo(inicioHoje, fimHoje);
        List<Reparacao> concluidasSemana = reparacaoRepository.findConcluidasNoPeriodo(inicioSemana, fimSemana);
        List<Reparacao> concluidasMes = reparacaoRepository.findConcluidasNoPeriodo(inicioMes, fimMes);

        BigDecimal faturacaoHoje = concluidasHoje.stream()
                .map(r -> r.getValorTotal() != null ? r.getValorTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal faturacaoSemana = concluidasSemana.stream()
                .map(r -> r.getValorTotal() != null ? r.getValorTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal faturacaoMes = concluidasMes.stream()
                .map(r -> r.getValorTotal() != null ? r.getValorTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long reparacoesEmCurso = reparacaoRepository.countAtivas();
        long concluidasHojeCount = concluidasHoje.size();
        long marcacoesFuturas = agendamentoRepository.countMarcacoesFuturas(LocalDateTime.now());
        long pecasStockBaixo = pecaRepository.findPecasComStockBaixo().size();

        String ocupacaoAtual = reparacoesEmCurso + "/" + CAPACIDADE_OFICINA;

        return new DashboardResponse(faturacaoHoje, faturacaoSemana, faturacaoMes,
                reparacoesEmCurso, concluidasHojeCount, ocupacaoAtual,
                marcacoesFuturas, pecasStockBaixo);
    }
}
