package com.gabrielveras.estoque_api.repository;

import com.gabrielveras.estoque_api.model.Movimentacao;
import com.gabrielveras.estoque_api.model.enums.TipoMovimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {
    List<Movimentacao> findByProdutoId(Long produtoId);
    List<Movimentacao> findByTipo(TipoMovimentacao tipo);
    List<Movimentacao> findByCriadoEmBetween(LocalDateTime inicio, LocalDateTime fim);
}