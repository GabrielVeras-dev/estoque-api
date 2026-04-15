package com.gabrielveras.estoque_api.service;

import com.gabrielveras.estoque_api.dto.request.CategoriaRequest;
import com.gabrielveras.estoque_api.dto.response.CategoriaResponse;
import com.gabrielveras.estoque_api.mapper.CategoriaMapper;
import com.gabrielveras.estoque_api.model.Categoria;
import com.gabrielveras.estoque_api.repository.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do CategoriaService")
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private CategoriaMapper categoriaMapper;

    @InjectMocks
    private CategoriaService categoriaService;

    private Categoria categoria;
    private CategoriaRequest request;
    private CategoriaResponse response;

    @BeforeEach
    void setUp() {
        categoria = Categoria.builder()
                .id(1L)
                .nome("Eletrônicos")
                .descricao("Produtos eletrônicos")
                .build();

        request = new CategoriaRequest();
        request.setNome("Eletrônicos");
        request.setDescricao("Produtos eletrônicos");

        response = new CategoriaResponse();
        response.setId(1L);
        response.setNome("Eletrônicos");
        response.setDescricao("Produtos eletrônicos");
    }

    @Test
    @DisplayName("Deve criar categoria com sucesso")
    void deveCriarCategoriaComSucesso() {
        when(categoriaRepository.existsByNome(request.getNome())).thenReturn(false);
        when(categoriaMapper.toEntity(request)).thenReturn(categoria);
        when(categoriaRepository.save(categoria)).thenReturn(categoria);
        when(categoriaMapper.toResponse(categoria)).thenReturn(response);

        CategoriaResponse resultado = categoriaService.criar(request);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("Eletrônicos");
        verify(categoriaRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar categoria com nome duplicado")
    void deveLancarExcecaoNomeDuplicado() {
        when(categoriaRepository.existsByNome(request.getNome())).thenReturn(true);

        assertThatThrownBy(() -> categoriaService.criar(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Categoria já cadastrada");

        verify(categoriaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve listar todas as categorias")
    void deveListarTodasCategorias() {
        when(categoriaRepository.findAll()).thenReturn(List.of(categoria));
        when(categoriaMapper.toResponse(categoria)).thenReturn(response);

        List<CategoriaResponse> resultado = categoriaService.listarTodos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNome()).isEqualTo("Eletrônicos");
    }

    @Test
    @DisplayName("Deve buscar categoria por ID com sucesso")
    void deveBuscarCategoriaPorId() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(categoriaMapper.toResponse(categoria)).thenReturn(response);

        CategoriaResponse resultado = categoriaService.buscarPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar categoria inexistente")
    void deveLancarExcecaoCategoriaNaoEncontrada() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoriaService.buscarPorId(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Categoria não encontrada");
    }

    @Test
    @DisplayName("Deve deletar categoria com sucesso")
    void deveDeletarCategoriaComSucesso() {
        when(categoriaRepository.existsById(1L)).thenReturn(true);

        categoriaService.deletar(1L);

        verify(categoriaRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar categoria inexistente")
    void deveLancarExcecaoAoDeletarInexistente() {
        when(categoriaRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> categoriaService.deletar(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Categoria não encontrada");

        verify(categoriaRepository, never()).deleteById(any());
    }
}