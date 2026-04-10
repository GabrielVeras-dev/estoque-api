package com.gabrielveras.estoque_api.mapper;

import com.gabrielveras.estoque_api.dto.request.ProdutoRequest;
import com.gabrielveras.estoque_api.dto.response.ProdutoResponse;
import com.gabrielveras.estoque_api.model.Produto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProdutoMapper {

    @Mapping(target = "categoria", ignore = true)
    @Mapping(target = "fornecedor", ignore = true)
    Produto toEntity(ProdutoRequest request);

    @Mapping(target = "categoriaNome", source = "categoria.nome")
    @Mapping(target = "fornecedorNome", source = "fornecedor.nome")
    @Mapping(target = "estoqueBaixo", expression = "java(produto.estoqueBaixo())")
    ProdutoResponse toResponse(Produto produto);

    @Mapping(target = "categoria", ignore = true)
    @Mapping(target = "fornecedor", ignore = true)
    void updateEntity(ProdutoRequest request, @MappingTarget Produto produto);
}