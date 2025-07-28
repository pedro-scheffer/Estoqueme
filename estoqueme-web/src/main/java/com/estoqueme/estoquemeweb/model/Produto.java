package com.estoqueme.estoquemeweb.model; // ESTA LINHA DEVE ESTAR EXATAMENTE ASSIM

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

/**
 * Representa a entidade Produto no sistema, mapeada para a tabela 'produtos' no banco de dados.
 */
@Entity
@Table(name = "produtos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String codigo;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(name = "preco_ultima_compra", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoUltimaCompra;

    @Column(name = "preco_medio", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoMedio;

    @Column(name = "porcentagem_lucro", nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentagemLucro;

    @Column(name = "preco_venda", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoVenda;

    @Column(name = "tipo_produto", nullable = false)
    private String tipoProduto;

    @Column(nullable = false)
    private String marca;

    @Column(nullable = false)
    private String categoria;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    // Construtor sem ID (útil para criar novos produtos antes de persistir)
    public Produto(String nome, String codigo, Integer quantidade, BigDecimal precoUltimaCompra,
                   BigDecimal precoMedio, BigDecimal porcentagemLucro, BigDecimal precoVenda,
                   String tipoProduto, String marca, String categoria, String descricao) {
        this.nome = nome;
        this.codigo = codigo;
        this.quantidade = quantidade;
        this.precoUltimaCompra = precoUltimaCompra;
        this.precoMedio = precoMedio;
        this.porcentagemLucro = porcentagemLucro;
        this.precoVenda = precoVenda;
        this.tipoProduto = tipoProduto;
        this.marca = marca;
        this.categoria = categoria;
        this.descricao = descricao;
    }
}