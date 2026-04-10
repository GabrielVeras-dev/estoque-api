package com.gabrielveras.estoque_api.service;

import com.gabrielveras.estoque_api.dto.request.ProdutoRequest;
import com.gabrielveras.estoque_api.dto.response.ProdutoResponse;
import com.gabrielveras.estoque_api.mapper.ProdutoMapper;
import com.gabrielveras.estoque_api.repository.CategoriaRepository;
import com.gabrielveras.estoque_api.repository.FornecedorRepository;
import com.gabrielveras.estoque_api.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;
    private final FornecedorRepository fornecedorRepository;
    private final ProdutoMapper produtoMapper;

    public ProdutoResponse criar(ProdutoRequest request) {
        if (request.getCodigo() != null && produtoRepository.existsByCodigo(request.getCodigo())) {
            throw new RuntimeException("Código de produto já cadastrado");
        }

        var produto = produtoMapper.toEntity(request);

        if (request.getCategoriaId() != null) {
            var categoria = categoriaRepository.findById(request.getCategoriaId())
                    .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
            produto.setCategoria(categoria);
        }

        if (request.getFornecedorId() != null) {
            var fornecedor = fornecedorRepository.findById(request.getFornecedorId())
                    .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado"));
            produto.setFornecedor(fornecedor);
        }

        return produtoMapper.toResponse(produtoRepository.save(produto));
    }

    public List<ProdutoResponse> listarTodos() {
        return produtoRepository.findAll()
                .stream()
                .map(produtoMapper::toResponse)
                .toList();
    }

    public List<ProdutoResponse> listarAtivos() {
        return produtoRepository.findByAtivoTrue()
                .stream()
                .map(produtoMapper::toResponse)
                .toList();
    }

    public List<ProdutoResponse> listarComEstoqueBaixo() {
        return produtoRepository.findProdutosComEstoqueBaixo()
                .stream()
                .map(produtoMapper::toResponse)
                .toList();
    }

    public ProdutoResponse buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .map(produtoMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
    }

    public ProdutoResponse atualizar(Long id, ProdutoRequest request) {
        var produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        produtoMapper.updateEntity(request, produto);

        if (request.getCategoriaId() != null) {
            var categoria = categoriaRepository.findById(request.getCategoriaId())
                    .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
            produto.setCategoria(categoria);
        }

        if (request.getFornecedorId() != null) {
            var fornecedor = fornecedorRepository.findById(request.getFornecedorId())
                    .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado"));
            produto.setFornecedor(fornecedor);
        }

        return produtoMapper.toResponse(produtoRepository.save(produto));
    }

    public ProdutoResponse alterarStatus(Long id, Boolean ativo) {
        var produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        produto.setAtivo(ativo);
        return produtoMapper.toResponse(produtoRepository.save(produto));
    }

    public void deletar(Long id) {
        if (!produtoRepository.existsById(id)) {
            throw new RuntimeException("Produto não encontrado");
        }
        produtoRepository.deleteById(id);
    }
}