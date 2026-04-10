package com.gabrielveras.estoque_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FornecedorRequest {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    private String cnpj;
    private String email;
    private String telefone;
    private String endereco;
}