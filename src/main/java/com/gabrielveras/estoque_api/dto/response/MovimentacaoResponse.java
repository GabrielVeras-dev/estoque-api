package com.gabrielveras.estoque_api.dto.response;

import com.gabrielveras.estoque_api.model.enums.TipoMovimentacao;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MovimentacaoResponse {
    private Long id;
    private TipoMovimentacao tipo;
    private Integer quantidade;
    private String motivo;
    private String produtoNome;
    private String usuarioNome;
    private LocalDateTime criadoEm;
}