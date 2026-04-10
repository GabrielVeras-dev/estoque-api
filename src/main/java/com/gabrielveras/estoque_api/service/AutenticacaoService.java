package com.gabrielveras.estoque_api.service;

import com.gabrielveras.estoque_api.dto.request.LoginRequest;
import com.gabrielveras.estoque_api.dto.request.RegistroRequest;
import com.gabrielveras.estoque_api.dto.response.AuthResponse;
import com.gabrielveras.estoque_api.model.Usuario;
import com.gabrielveras.estoque_api.model.enums.PerfilUsuario;
import com.gabrielveras.estoque_api.repository.UsuarioRepository;
import com.gabrielveras.estoque_api.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AutenticacaoService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse registrar(RegistroRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email já cadastrado");
        }

        var usuario = Usuario.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .senha(passwordEncoder.encode(request.getSenha()))
                .perfil(PerfilUsuario.OPERADOR)
                .build();

        usuarioRepository.save(usuario);

        return gerarAuthResponse(usuario);
    }

    public AuthResponse registrarAdmin(RegistroRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email já cadastrado");
        }

        var usuario = Usuario.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .senha(passwordEncoder.encode(request.getSenha()))
                .perfil(PerfilUsuario.ADMIN)
                .build();

        usuarioRepository.save(usuario);

        return gerarAuthResponse(usuario);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getSenha()
                )
        );

        var usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return gerarAuthResponse(usuario);
    }

    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtService.isRefreshToken(refreshToken)) {
            throw new RuntimeException("Token inválido");
        }

        String email = jwtService.extrairEmail(refreshToken);
        var usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return gerarAuthResponse(usuario);
    }

    private AuthResponse gerarAuthResponse(Usuario usuario) {
        String token = jwtService.gerarToken(usuario);
        String refreshToken = jwtService.gerarRefreshToken(usuario);

        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .tipo("Bearer")
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .perfil(usuario.getPerfil().name())
                .build();
    }
}