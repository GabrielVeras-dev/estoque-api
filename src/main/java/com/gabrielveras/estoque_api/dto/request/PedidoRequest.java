package com.gabrielveras.estoque_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
public class PedidoRequest {

    @NotBlank(message = "Cliente é obrigatório")
    private String cliente;

    private String observacao;

    @NotEmpty(message = "Pedido deve ter pelo menos um item")
    private List<ItemPedidoRequest> itens;

    @Data
    public static class ItemPedidoRequest {

        @NotNull(message = "Produto é obrigatório")
        private Long produtoId;

        @NotNull(message = "Quantidade é obrigatória")
        @Positive(message = "Quantidade deve ser maior que zero")
        private Integer quantidade;
    }
}