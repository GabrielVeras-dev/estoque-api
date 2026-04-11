package com.gabrielveras.estoque_api.controller;

import com.gabrielveras.estoque_api.dto.request.FornecedorRequest;
import com.gabrielveras.estoque_api.dto.response.FornecedorResponse;
import com.gabrielveras.estoque_api.service.FornecedorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fornecedores")
@RequiredArgsConstructor
public class FornecedorController {

    private final FornecedorService fornecedorService;

    @PostMapping
    public ResponseEntity<FornecedorResponse> criar(@Valid @RequestBody FornecedorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fornecedorService.criar(request));
    }

    @GetMapping
    public ResponseEntity<List<FornecedorResponse>> listarTodos() {
        return ResponseEntity.ok(fornecedorService.listarTodos());
    }

    @GetMapping("/ativos")
    public ResponseEntity<List<FornecedorResponse>> listarAtivos() {
        return ResponseEntity.ok(fornecedorService.listarAtivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FornecedorResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(fornecedorService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FornecedorResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody FornecedorRequest request) {
        return ResponseEntity.ok(fornecedorService.atualizar(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<FornecedorResponse> alterarStatus(
            @PathVariable Long id,
            @RequestParam Boolean ativo) {
        return ResponseEntity.ok(fornecedorService.alterarStatus(id, ativo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        fornecedorService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}