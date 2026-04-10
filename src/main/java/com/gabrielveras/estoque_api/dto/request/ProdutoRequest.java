package com.gabrielveras.estoque_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProdutoRequest {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    private String descricao;
    private String codigo;

    @NotNull(message = "Preço de custo é obrigatório")
    @PositiveOrZero(message = "Preço de custo deve ser positivo")
    private BigDecimal precoCusto;

    @NotNull(message = "Preço de venda é obrigatório")
    @PositiveOrZero(message = "Preço de venda deve ser positivo")
    private BigDecimal precoVenda;

    @NotNull(message = "Quantidade em estoque é obrigatória")
    @PositiveOrZero(message = "Quantidade deve ser positiva")
    private Integer quantidadeEstoque;

    @NotNull(message = "Estoque mínimo é obrigatório")
    @PositiveOrZero(message = "Estoque mínimo deve ser positivo")
    private Integer estoqueMinimo;

    private Long categoriaId;
    private Long fornecedorId;
}