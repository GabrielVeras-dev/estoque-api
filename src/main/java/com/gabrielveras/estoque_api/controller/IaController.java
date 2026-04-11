package com.gabrielveras.estoque_api.controller;

import com.gabrielveras.estoque_api.ia.EstoqueIaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/ia")
@RequiredArgsConstructor
public class IaController {

    private final EstoqueIaService estoqueIaService;

    @GetMapping("/estoque-baixo")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Map<String, String>> analisarEstoqueBaixo() {
        String analise = estoqueIaService.analisarEstoqueBaixo();
        return ResponseEntity.ok(Map.of("analise", analise));
    }

    @GetMapping("/produto/{id}/mercado")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Map<String, String>> analisarMercadoProduto(@PathVariable Long id) {
        String analise = estoqueIaService.analisarMercadoProduto(id);
        return ResponseEntity.ok(Map.of("analise", analise));
    }

    @GetMapping("/sugestoes")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Map<String, String>> sugerirProdutosEmAlta(
            @RequestParam String categoria) {
        String sugestoes = estoqueIaService.sugerirProdutosEmAlta(categoria);
        return ResponseEntity.ok(Map.of("sugestoes", sugestoes));
    }

    @GetMapping("/relatorio-executivo")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> gerarRelatorioExecutivo() {
        String relatorio = estoqueIaService.gerarRelatorioExecutivo();
        return ResponseEntity.ok(Map.of("relatorio", relatorio));
    }
}