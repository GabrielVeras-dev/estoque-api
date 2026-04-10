package com.gabrielveras.estoque_api.mapper;

import com.gabrielveras.estoque_api.dto.response.PedidoResponse;
import com.gabrielveras.estoque_api.model.ItemPedido;
import com.gabrielveras.estoque_api.model.Pedido;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PedidoMapper {

    @Mapping(target = "usuarioNome", source = "usuario.nome")
    PedidoResponse toResponse(Pedido pedido);

    @Mapping(target = "produtoNome", source = "produto.nome")
    PedidoResponse.ItemPedidoResponse toItemResponse(ItemPedido item);
}