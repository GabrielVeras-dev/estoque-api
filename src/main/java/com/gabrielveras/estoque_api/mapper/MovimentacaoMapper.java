package com.gabrielveras.estoque_api.mapper;

import com.gabrielveras.estoque_api.dto.response.MovimentacaoResponse;
import com.gabrielveras.estoque_api.model.Movimentacao;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MovimentacaoMapper {

    @Mapping(target = "produtoNome", source = "produto.nome")
    @Mapping(target = "usuarioNome", source = "usuario.nome")
    MovimentacaoResponse toResponse(Movimentacao movimentacao);
}