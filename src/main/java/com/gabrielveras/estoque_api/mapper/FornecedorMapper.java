package com.gabrielveras.estoque_api.mapper;

import com.gabrielveras.estoque_api.dto.request.FornecedorRequest;
import com.gabrielveras.estoque_api.dto.response.FornecedorResponse;
import com.gabrielveras.estoque_api.model.Fornecedor;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FornecedorMapper {
    Fornecedor toEntity(FornecedorRequest request);
    FornecedorResponse toResponse(Fornecedor fornecedor);
    void updateEntity(FornecedorRequest request, @MappingTarget Fornecedor fornecedor);
}