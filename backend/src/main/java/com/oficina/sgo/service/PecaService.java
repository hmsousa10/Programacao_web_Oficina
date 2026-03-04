package com.oficina.sgo.service;

import com.oficina.sgo.dto.request.CreatePecaRequest;
import com.oficina.sgo.dto.request.RequisitarPecaRequest;
import com.oficina.sgo.dto.request.StockMovimentoRequest;
import com.oficina.sgo.dto.response.PecaResponse;
import com.oficina.sgo.exception.BusinessException;
import com.oficina.sgo.exception.ResourceNotFoundException;
import com.oficina.sgo.model.MovimentoStock;
import com.oficina.sgo.model.Peca;
import com.oficina.sgo.model.Reparacao;
import com.oficina.sgo.repository.MovimentoStockRepository;
import com.oficina.sgo.repository.PecaRepository;
import com.oficina.sgo.repository.ReparacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PecaService {

    private final PecaRepository pecaRepository;
    private final MovimentoStockRepository movimentoStockRepository;
    private final ReparacaoRepository reparacaoRepository;

    public List<PecaResponse> findAll() {
        return pecaRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public PecaResponse findById(Long id) {
        return toResponse(pecaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Peca", id)));
    }

    public List<PecaResponse> findAlertasStock() {
        return pecaRepository.findPecasComStockBaixo().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public PecaResponse create(CreatePecaRequest request) {
        if (pecaRepository.existsByReferencia(request.referencia())) {
            throw new BusinessException("Referencia already exists: " + request.referencia());
        }
        Peca peca = Peca.builder()
                .referencia(request.referencia())
                .designacao(request.designacao())
                .precoUnitario(request.precoUnitario())
                .quantidadeStock(request.quantidadeStock() != null ? request.quantidadeStock() : 0)
                .stockMinimo(request.stockMinimo() != null ? request.stockMinimo() : 0)
                .categoria(request.categoria())
                .fornecedor(request.fornecedor())
                .build();
        return toResponse(pecaRepository.save(peca));
    }

    @Transactional
    public PecaResponse update(Long id, CreatePecaRequest request) {
        Peca peca = pecaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Peca", id));
        if (!peca.getReferencia().equals(request.referencia()) && pecaRepository.existsByReferencia(request.referencia())) {
            throw new BusinessException("Referencia already exists: " + request.referencia());
        }
        peca.setReferencia(request.referencia());
        peca.setDesignacao(request.designacao());
        peca.setPrecoUnitario(request.precoUnitario());
        if (request.stockMinimo() != null) peca.setStockMinimo(request.stockMinimo());
        if (request.categoria() != null) peca.setCategoria(request.categoria());
        if (request.fornecedor() != null) peca.setFornecedor(request.fornecedor());
        return toResponse(pecaRepository.save(peca));
    }

    @Transactional
    public void delete(Long id) {
        Peca peca = pecaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Peca", id));
        pecaRepository.delete(peca);
    }

    @Transactional
    public PecaResponse entradaStock(Long id, StockMovimentoRequest request) {
        Peca peca = pecaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Peca", id));
        peca.setQuantidadeStock(peca.getQuantidadeStock() + request.quantidade());
        MovimentoStock movimento = MovimentoStock.builder()
                .peca(peca)
                .tipoMovimento(MovimentoStock.TipoMovimento.ENTRADA)
                .quantidade(request.quantidade())
                .dataMovimento(LocalDateTime.now())
                .observacoes(request.observacoes())
                .build();
        movimentoStockRepository.save(movimento);
        return toResponse(pecaRepository.save(peca));
    }

    @Transactional
    public PecaResponse saidaStock(Long id, StockMovimentoRequest request) {
        Peca peca = pecaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Peca", id));
        if (peca.getQuantidadeStock() < request.quantidade()) {
            throw new BusinessException("Insufficient stock. Available: " + peca.getQuantidadeStock());
        }
        peca.setQuantidadeStock(peca.getQuantidadeStock() - request.quantidade());
        MovimentoStock movimento = MovimentoStock.builder()
                .peca(peca)
                .tipoMovimento(MovimentoStock.TipoMovimento.SAIDA)
                .quantidade(request.quantidade())
                .dataMovimento(LocalDateTime.now())
                .observacoes(request.observacoes())
                .build();
        movimentoStockRepository.save(movimento);
        return toResponse(pecaRepository.save(peca));
    }

    @Transactional
    public PecaResponse requisitarPeca(RequisitarPecaRequest request) {
        Peca peca = pecaRepository.findById(request.pecaId())
                .orElseThrow(() -> new ResourceNotFoundException("Peca", request.pecaId()));
        if (peca.getQuantidadeStock() < request.quantidade()) {
            throw new BusinessException("Insufficient stock. Available: " + peca.getQuantidadeStock());
        }
        Reparacao reparacao = reparacaoRepository.findById(request.reparacaoId())
                .orElseThrow(() -> new ResourceNotFoundException("Reparacao", request.reparacaoId()));
        peca.setQuantidadeStock(peca.getQuantidadeStock() - request.quantidade());
        MovimentoStock movimento = MovimentoStock.builder()
                .peca(peca)
                .tipoMovimento(MovimentoStock.TipoMovimento.REQUISICAO_REPARACAO)
                .quantidade(request.quantidade())
                .dataMovimento(LocalDateTime.now())
                .reparacao(reparacao)
                .observacoes(request.observacoes())
                .build();
        movimentoStockRepository.save(movimento);
        return toResponse(pecaRepository.save(peca));
    }

    private PecaResponse toResponse(Peca p) {
        return new PecaResponse(p.getId(), p.getReferencia(), p.getDesignacao(),
                p.getQuantidadeStock(), p.getStockMinimo(), p.getPrecoUnitario(),
                p.getCategoria(), p.getFornecedor(),
                p.getQuantidadeStock() <= p.getStockMinimo());
    }
}
