package com.gabrielveras.estoque_api.service;

import com.gabrielveras.estoque_api.dto.response.MovimentacaoResponse;
import com.gabrielveras.estoque_api.mapper.MovimentacaoMapper;
import com.gabrielveras.estoque_api.model.Movimentacao;
import com.gabrielveras.estoque_api.model.Usuario;
import com.gabrielveras.estoque_api.model.enums.TipoMovimentacao;
import com.gabrielveras.estoque_api.repository.MovimentacaoRepository;
import com.gabrielveras.estoque_api.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovimentacaoService {

    private final MovimentacaoRepository movimentacaoRepository;
    private final ProdutoRepository produtoRepository;
    private final MovimentacaoMapper movimentacaoMapper;

    @Transactional
    public MovimentacaoResponse registrar(Long produtoId, TipoMovimentacao tipo,
                                          Integer quantidade, String motivo, Usuario usuario) {
        var produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        switch (tipo) {
            case ENTRADA, DEVOLUCAO ->
                    produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() + quantidade);
            case SAIDA -> {
                if (produto.getQuantidadeEstoque() < quantidade) {
                    throw new RuntimeException("Estoque insuficiente");
                }
                produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - quantidade);
            }
            case AJUSTE ->
                    produto.setQuantidadeEstoque(quantidade);
        }

        produtoRepository.save(produto);

        var movimentacao = Movimentacao.builder()
                .produto(produto)
                .tipo(tipo)
                .quantidade(quantidade)
                .motivo(motivo)
                .usuario(usuario)
                .build();

        return movimentacaoMapper.toResponse(movimentacaoRepository.save(movimentacao));
    }

    public List<MovimentacaoResponse> listarTodos() {
        return movimentacaoRepository.findAll()
                .stream()
                .map(movimentacaoMapper::toResponse)
                .toList();
    }

    public List<MovimentacaoResponse> listarPorProduto(Long produtoId) {
        return movimentacaoRepository.findByProdutoId(produtoId)
                .stream()
                .map(movimentacaoMapper::toResponse)
                .toList();
    }
}