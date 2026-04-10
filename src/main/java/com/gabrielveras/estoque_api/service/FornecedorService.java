package com.gabrielveras.estoque_api.service;

import com.gabrielveras.estoque_api.dto.request.FornecedorRequest;
import com.gabrielveras.estoque_api.dto.response.FornecedorResponse;
import com.gabrielveras.estoque_api.mapper.FornecedorMapper;
import com.gabrielveras.estoque_api.repository.FornecedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FornecedorService {

    private final FornecedorRepository fornecedorRepository;
    private final FornecedorMapper fornecedorMapper;

    public FornecedorResponse criar(FornecedorRequest request) {
        if (request.getCnpj() != null && fornecedorRepository.existsByCnpj(request.getCnpj())) {
            throw new RuntimeException("CNPJ já cadastrado");
        }
        var fornecedor = fornecedorMapper.toEntity(request);
        return fornecedorMapper.toResponse(fornecedorRepository.save(fornecedor));
    }

    public List<FornecedorResponse> listarTodos() {
        return fornecedorRepository.findAll()
                .stream()
                .map(fornecedorMapper::toResponse)
                .toList();
    }

    public List<FornecedorResponse> listarAtivos() {
        return fornecedorRepository.findByAtivoTrue()
                .stream()
                .map(fornecedorMapper::toResponse)
                .toList();
    }

    public FornecedorResponse buscarPorId(Long id) {
        return fornecedorRepository.findById(id)
                .map(fornecedorMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado"));
    }

    public FornecedorResponse atualizar(Long id, FornecedorRequest request) {
        var fornecedor = fornecedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado"));
        fornecedorMapper.updateEntity(request, fornecedor);
        return fornecedorMapper.toResponse(fornecedorRepository.save(fornecedor));
    }

    public FornecedorResponse alterarStatus(Long id, Boolean ativo) {
        var fornecedor = fornecedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado"));
        fornecedor.setAtivo(ativo);
        return fornecedorMapper.toResponse(fornecedorRepository.save(fornecedor));
    }

    public void deletar(Long id) {
        if (!fornecedorRepository.existsById(id)) {
            throw new RuntimeException("Fornecedor não encontrado");
        }
        fornecedorRepository.deleteById(id);
    }
}