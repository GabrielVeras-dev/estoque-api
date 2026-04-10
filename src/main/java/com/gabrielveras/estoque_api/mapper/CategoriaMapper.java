package com.gabrielveras.estoque_api.mapper;

import com.gabrielveras.estoque_api.dto.request.CategoriaRequest;
import com.gabrielveras.estoque_api.dto.response.CategoriaResponse;
import com.gabrielveras.estoque_api.model.Categoria;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoriaMapper {
    Categoria toEntity(CategoriaRequest request);
    CategoriaResponse toResponse(Categoria categoria);
    void updateEntity(CategoriaRequest request, @MappingTarget Categoria categoria);
}