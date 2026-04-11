package com.gabrielveras.estoque_api.controller;

import com.gabrielveras.estoque_api.dto.response.MovimentacaoResponse;
import com.gabrielveras.estoque_api.model.Usuario;
import com.gabrielveras.estoque_api.model.enums.TipoMovimentacao;
import com.gabrielveras.estoque_api.service.MovimentacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movimentacoes")
@RequiredArgsConstructor
public class MovimentacaoController {

    private final MovimentacaoService movimentacaoService;

    @PostMapping("/produto/{produtoId}")
    public ResponseEntity<MovimentacaoResponse> registrar(
            @PathVariable Long produtoId,
            @RequestParam TipoMovimentacao tipo,
            @RequestParam Integer quantidade,
            @RequestParam(required = false) String motivo,
            @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(movimentacaoService.registrar(produtoId, tipo, quantidade, motivo, usuario));
    }

    @GetMapping
    public ResponseEntity<List<MovimentacaoResponse>> listarTodos() {
        return ResponseEntity.ok(movimentacaoService.listarTodos());
    }

    @GetMapping("/produto/{produtoId}")
    public ResponseEntity<List<MovimentacaoResponse>> listarPorProduto(@PathVariable Long produtoId) {
        return ResponseEntity.ok(movimentacaoService.listarPorProduto(produtoId));
    }
}