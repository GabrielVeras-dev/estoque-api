package com.gabrielveras.estoque_api.service;

import com.gabrielveras.estoque_api.dto.request.PedidoRequest;
import com.gabrielveras.estoque_api.dto.response.PedidoResponse;
import com.gabrielveras.estoque_api.mapper.PedidoMapper;
import com.gabrielveras.estoque_api.model.ItemPedido;
import com.gabrielveras.estoque_api.model.Pedido;
import com.gabrielveras.estoque_api.model.Usuario;
import com.gabrielveras.estoque_api.model.enums.StatusPedido;
import com.gabrielveras.estoque_api.repository.PedidoRepository;
import com.gabrielveras.estoque_api.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final PedidoMapper pedidoMapper;

    @Transactional
    public PedidoResponse criar(PedidoRequest request, Usuario usuario) {
        var pedido = Pedido.builder()
                .numeroPedido(gerarNumeroPedido())
                .cliente(request.getCliente())
                .observacao(request.getObservacao())
                .status(StatusPedido.PENDENTE)
                .usuario(usuario)
                .build();

        List<ItemPedido> itens = request.getItens().stream().map(itemRequest -> {
            var produto = produtoRepository.findById(itemRequest.getProdutoId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + itemRequest.getProdutoId()));

            BigDecimal subtotal = produto.getPrecoVenda()
                    .multiply(BigDecimal.valueOf(itemRequest.getQuantidade()));

            return ItemPedido.builder()
                    .produto(produto)
                    .quantidade(itemRequest.getQuantidade())
                    .precoUnitario(produto.getPrecoVenda())
                    .subtotal(subtotal)
                    .pedido(pedido)
                    .build();
        }).toList();

        pedido.setItens(itens);
        pedido.setValorTotal(itens.stream()
                .map(ItemPedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        return pedidoMapper.toResponse(pedidoRepository.save(pedido));
    }

    @Transactional
    public PedidoResponse confirmar(Long id) {
        var pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        if (pedido.getStatus() != StatusPedido.PENDENTE) {
            throw new RuntimeException("Apenas pedidos PENDENTES podem ser confirmados");
        }

        pedido.getItens().forEach(item -> {
            var produto = item.getProduto();
            if (produto.getQuantidadeEstoque() < item.getQuantidade()) {
                throw new RuntimeException("Estoque insuficiente para: " + produto.getNome());
            }
            produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - item.getQuantidade());
            produtoRepository.save(produto);
        });

        pedido.setStatus(StatusPedido.CONFIRMADO);
        return pedidoMapper.toResponse(pedidoRepository.save(pedido));
    }

    @Transactional
    public PedidoResponse avancarStatus(Long id) {
        var pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        StatusPedido novoStatus = switch (pedido.getStatus()) {
            case CONFIRMADO -> StatusPedido.ENVIADO;
            case ENVIADO    -> StatusPedido.ENTREGUE;
            default -> throw new RuntimeException("Status não pode ser avançado: " + pedido.getStatus());
        };

        pedido.setStatus(novoStatus);
        return pedidoMapper.toResponse(pedidoRepository.save(pedido));
    }

    @Transactional
    public PedidoResponse cancelar(Long id) {
        var pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        if (pedido.getStatus() == StatusPedido.ENTREGUE) {
            throw new RuntimeException("Pedidos entregues não podem ser cancelados");
        }

        if (pedido.getStatus() == StatusPedido.CONFIRMADO ||
                pedido.getStatus() == StatusPedido.ENVIADO) {
            pedido.getItens().forEach(item -> {
                var produto = item.getProduto();
                produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() + item.getQuantidade());
                produtoRepository.save(produto);
            });
        }

        pedido.setStatus(StatusPedido.CANCELADO);
        return pedidoMapper.toResponse(pedidoRepository.save(pedido));
    }

    public List<PedidoResponse> listarTodos() {
        return pedidoRepository.findAll()
                .stream()
                .map(pedidoMapper::toResponse)
                .toList();
    }

    public List<PedidoResponse> listarPorStatus(StatusPedido status) {
        return pedidoRepository.findByStatus(status)
                .stream()
                .map(pedidoMapper::toResponse)
                .toList();
    }

    public PedidoResponse buscarPorId(Long id) {
        return pedidoRepository.findById(id)
                .map(pedidoMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
    }

    private String gerarNumeroPedido() {
        String data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuid = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "PED-" + data + "-" + uuid;
    }
}