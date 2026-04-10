package com.gabrielveras.estoque_api.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FornecedorResponse {
    private Long id;
    private String nome;
    private String cnpj;
    private String email;
    private String telefone;
    private String endereco;
    private Boolean ativo;
    private LocalDateTime criadoEm;
}