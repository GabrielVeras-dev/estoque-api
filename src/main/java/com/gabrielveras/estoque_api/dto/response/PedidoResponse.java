package com.gabrielveras.estoque_api.dto.response;

import com.gabrielveras.estoque_api.model.enums.StatusPedido;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PedidoResponse {
    private Long id;
    private String numeroPedido;
    private String cliente;
    private StatusPedido status;
    private BigDecimal valorTotal;
    private String observacao;
    private List<ItemPedidoResponse> itens;
    private String usuarioNome;
    private LocalDateTime criadoEm;

    @Data
    public static class ItemPedidoResponse {
        private Long id;
        private String produtoNome;
        private Integer quantidade;
        private BigDecimal precoUnitario;
        private BigDecimal subtotal;
    }
}