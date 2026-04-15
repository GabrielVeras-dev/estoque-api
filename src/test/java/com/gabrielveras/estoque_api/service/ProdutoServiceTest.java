package com.gabrielveras.estoque_api.service;

import com.gabrielveras.estoque_api.dto.request.ProdutoRequest;
import com.gabrielveras.estoque_api.dto.response.ProdutoResponse;
import com.gabrielveras.estoque_api.mapper.ProdutoMapper;
import com.gabrielveras.estoque_api.model.Produto;
import com.gabrielveras.estoque_api.repository.CategoriaRepository;
import com.gabrielveras.estoque_api.repository.FornecedorRepository;
import com.gabrielveras.estoque_api.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do ProdutoService")
class ProdutoServiceTest {

    @Mock private ProdutoRepository produtoRepository;
    @Mock private CategoriaRepository categoriaRepository;
    @Mock private FornecedorRepository fornecedorRepository;
    @Mock private ProdutoMapper produtoMapper;

    @InjectMocks
    private ProdutoService produtoService;

    private Produto produto;
    private ProdutoRequest request;
    private ProdutoResponse response;

    @BeforeEach
    void setUp() {
        produto = Produto.builder()
                .id(1L)
                .nome("Notebook")
                .codigo("NB-001")
                .precoCusto(new BigDecimal("2000.00"))
                .precoVenda(new BigDecimal("3000.00"))
                .quantidadeEstoque(10)
                .estoqueMinimo(5)
                .ativo(true)
                .build();

        request = new ProdutoRequest();
        request.setNome("Notebook");
        request.setCodigo("NB-001");
        request.setPrecoCusto(new BigDecimal("2000.00"));
        request.setPrecoVenda(new BigDecimal("3000.00"));
        request.setQuantidadeEstoque(10);
        request.setEstoqueMinimo(5);

        response = new ProdutoResponse();
        response.setId(1L);
        response.setNome("Notebook");
        response.setCodigo("NB-001");
        response.setQuantidadeEstoque(10);
        response.setEstoqueMinimo(5);
        response.setAtivo(true);
    }

    @Test
    @DisplayName("Deve criar produto com sucesso")
    void deveCriarProdutoComSucesso() {
        when(produtoRepository.existsByCodigo("NB-001")).thenReturn(false);
        when(produtoMapper.toEntity(request)).thenReturn(produto);
        when(produtoRepository.save(produto)).thenReturn(produto);
        when(produtoMapper.toResponse(produto)).thenReturn(response);

        ProdutoResponse resultado = produtoService.criar(request);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("Notebook");
        verify(produtoRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar produto com código duplicado")
    void deveLancarExcecaoCodigoDuplicado() {
        when(produtoRepository.existsByCodigo("NB-001")).thenReturn(true);

        assertThatThrownBy(() -> produtoService.criar(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Código de produto já cadastrado");

        verify(produtoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve listar produtos com estoque baixo")
    void deveListarProdutosComEstoqueBaixo() {
        produto.setQuantidadeEstoque(2);
        response.setQuantidadeEstoque(2);
        response.setEstoqueBaixo(true);

        when(produtoRepository.findProdutosComEstoqueBaixo()).thenReturn(List.of(produto));
        when(produtoMapper.toResponse(produto)).thenReturn(response);

        List<ProdutoResponse> resultado = produtoService.listarComEstoqueBaixo();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getEstoqueBaixo()).isTrue();
    }

    @Test
    @DisplayName("Deve alterar status do produto")
    void deveAlterarStatusProduto() {
        response.setAtivo(false);

        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(produtoRepository.save(produto)).thenReturn(produto);
        when(produtoMapper.toResponse(produto)).thenReturn(response);

        ProdutoResponse resultado = produtoService.alterarStatus(1L, false);

        assertThat(resultado.getAtivo()).isFalse();
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar produto inexistente")
    void deveLancarExcecaoProdutoNaoEncontrado() {
        when(produtoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> produtoService.buscarPorId(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Produto não encontrado");
    }
}