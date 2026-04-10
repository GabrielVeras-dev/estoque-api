package com.gabrielveras.estoque_api.repository;

import com.gabrielveras.estoque_api.model.Pedido;
import com.gabrielveras.estoque_api.model.enums.StatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    Optional<Pedido> findByNumeroPedido(String numeroPedido);
    List<Pedido> findByStatus(StatusPedido status);
    List<Pedido> findByUsuarioId(Long usuarioId);
    List<Pedido> findByCriadoEmBetween(LocalDateTime inicio, LocalDateTime fim);
}