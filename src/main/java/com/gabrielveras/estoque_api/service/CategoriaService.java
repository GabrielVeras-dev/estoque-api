package com.gabrielveras.estoque_api.service;

import com.gabrielveras.estoque_api.dto.request.CategoriaRequest;
import com.gabrielveras.estoque_api.dto.response.CategoriaResponse;
import com.gabrielveras.estoque_api.mapper.CategoriaMapper;
import com.gabrielveras.estoque_api.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    public CategoriaResponse criar(CategoriaRequest request) {
        if (categoriaRepository.existsByNome(request.getNome())) {
            throw new RuntimeException("Categoria já cadastrada");
        }
        var categoria = categoriaMapper.toEntity(request);
        return categoriaMapper.toResponse(categoriaRepository.save(categoria));
    }

    public List<CategoriaResponse> listarTodos() {
        return categoriaRepository.findAll()
                .stream()
                .map(categoriaMapper::toResponse)
                .toList();
    }

    public CategoriaResponse buscarPorId(Long id) {
        return categoriaRepository.findById(id)
                .map(categoriaMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
    }

    public CategoriaResponse atualizar(Long id, CategoriaRequest request) {
        var categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
        categoriaMapper.updateEntity(request, categoria);
        return categoriaMapper.toResponse(categoriaRepository.save(categoria));
    }

    public void deletar(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new RuntimeException("Categoria não encontrada");
        }
        categoriaRepository.deleteById(id);
    }
}