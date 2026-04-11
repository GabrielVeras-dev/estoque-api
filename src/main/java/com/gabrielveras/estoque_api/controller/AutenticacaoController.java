package com.gabrielveras.estoque_api.controller;

import com.gabrielveras.estoque_api.dto.request.LoginRequest;
import com.gabrielveras.estoque_api.dto.request.RegistroRequest;
import com.gabrielveras.estoque_api.dto.response.AuthResponse;
import com.gabrielveras.estoque_api.service.AutenticacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AutenticacaoController {

    private final AutenticacaoService autenticacaoService;

    @PostMapping("/registrar")
    public ResponseEntity<AuthResponse> registrar(@Valid @RequestBody RegistroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(autenticacaoService.registrar(request));
    }

    @PostMapping("/registrar-admin")
    public ResponseEntity<AuthResponse> registrarAdmin(@Valid @RequestBody RegistroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(autenticacaoService.registrarAdmin(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(autenticacaoService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestHeader("Refresh-Token") String refreshToken) {
        return ResponseEntity.ok(autenticacaoService.refreshToken(refreshToken));
    }
}