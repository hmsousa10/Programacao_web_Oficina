package com.oficina.sgo.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "pecas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Peca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String referencia;

    @Column(nullable = false)
    private String designacao;

    @Column(nullable = false)
    @Builder.Default
    private Integer quantidadeStock = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer stockMinimo = 0;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precoUnitario;

    private String categoria;
    private String fornecedor;
}
