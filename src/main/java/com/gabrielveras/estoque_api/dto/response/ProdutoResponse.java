package com.gabrielveras.estoque_api.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProdutoResponse {
    private Long id;
    private String nome;
    private String descricao;
    private String codigo;
    private BigDecimal precoCusto;
    private BigDecimal precoVenda;
    private Integer quantidadeEstoque;
    private Integer estoqueMinimo;
    private Boolean estoqueBaixo;
    private Boolean ativo;
    private String categoriaNome;
    private String fornecedorNome;
    private LocalDateTime criadoEm;
}