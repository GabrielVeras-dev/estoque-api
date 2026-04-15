package com.gabrielveras.estoque_api.service;

import com.gabrielveras.estoque_api.dto.request.LoginRequest;
import com.gabrielveras.estoque_api.dto.request.RegistroRequest;
import com.gabrielveras.estoque_api.dto.response.AuthResponse;
import com.gabrielveras.estoque_api.model.Usuario;
import com.gabrielveras.estoque_api.model.enums.PerfilUsuario;
import com.gabrielveras.estoque_api.repository.UsuarioRepository;
import com.gabrielveras.estoque_api.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do AutenticacaoService")
class AutenticacaoServiceTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks
    private AutenticacaoService autenticacaoService;

    private Usuario usuario;
    private RegistroRequest registroRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .id(1L)
                .nome("Gabriel Veras")
                .email("gabriel@email.com")
                .senha("senha_criptografada")
                .perfil(PerfilUsuario.OPERADOR)
                .build();

        registroRequest = new RegistroRequest();
        registroRequest.setNome("Gabriel Veras");
        registroRequest.setEmail("gabriel@email.com");
        registroRequest.setSenha("Senha@123");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("gabriel@email.com");
        loginRequest.setSenha("Senha@123");
    }

    @Test
    @DisplayName("Deve registrar usuário com sucesso")
    void deveRegistrarUsuarioComSucesso() {
        when(usuarioRepository.existsByEmail(registroRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registroRequest.getSenha())).thenReturn("senha_criptografada");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(jwtService.gerarToken(any())).thenReturn("token_jwt");
        when(jwtService.gerarRefreshToken(any())).thenReturn("refresh_token");

        AuthResponse resultado = autenticacaoService.registrar(registroRequest);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getEmail()).isEqualTo("gabriel@email.com");
        assertThat(resultado.getToken()).isEqualTo("token_jwt");
        verify(usuarioRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao registrar email duplicado")
    void deveLancarExcecaoEmailDuplicado() {
        when(usuarioRepository.existsByEmail(registroRequest.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> autenticacaoService.registrar(registroRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Email já cadastrado");

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve fazer login com sucesso")
    void deveFazerLoginComSucesso() {
        when(authenticationManager.authenticate(any())).thenReturn(
                new UsernamePasswordAuthenticationToken("gabriel@email.com", "Senha@123")
        );
        when(usuarioRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(usuario));
        when(jwtService.gerarToken(any())).thenReturn("token_jwt");
        when(jwtService.gerarRefreshToken(any())).thenReturn("refresh_token");

        AuthResponse resultado = autenticacaoService.login(loginRequest);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getToken()).isEqualTo("token_jwt");
        assertThat(resultado.getPerfil()).isEqualTo("OPERADOR");
    }

    @Test
    @DisplayName("Deve lançar exceção com credenciais inválidas")
    void deveLancarExcecaoCredenciaisInvalidas() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Credenciais inválidas"));

        assertThatThrownBy(() -> autenticacaoService.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class);
    }
}