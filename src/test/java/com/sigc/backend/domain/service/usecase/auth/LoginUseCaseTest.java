package com.sigc.backend.domain.service.usecase.auth;

import com.sigc.backend.domain.exception.CredentialsInvalidException;
import com.sigc.backend.domain.exception.UserNotFoundException;
import com.sigc.backend.domain.model.Usuario;
import com.sigc.backend.domain.port.IUsuarioRepository;
import com.sigc.backend.infrastructure.security.jwt.ITokenProvider;
import com.sigc.backend.infrastructure.security.password.IPasswordEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LoginUseCaseTest {

    private IUsuarioRepository usuarioRepository;
    private ITokenProvider tokenProvider;
    private IPasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        usuarioRepository = Mockito.mock(IUsuarioRepository.class);
        tokenProvider = Mockito.mock(ITokenProvider.class);
        passwordEncoder = Mockito.mock(IPasswordEncoder.class);
    }

    @Test
    void executeSuccessReturnsToken() {
        var usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("user@example.com");
        usuario.setPassword("hashed");
        usuario.setRole("USER");

        when(usuarioRepository.findByEmail("user@example.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matchesPassword("Secret123", "hashed")).thenReturn(true);
        when(tokenProvider.generateToken(1L, "user@example.com", "USER")).thenReturn("jwt-token");

        var useCase = new LoginUseCase(usuarioRepository, tokenProvider, passwordEncoder);
        var resp = useCase.execute(new LoginRequest("user@example.com", "Secret123"));

        assertNotNull(resp);
        assertEquals("jwt-token", resp.getToken());
        verify(usuarioRepository, times(1)).findByEmail("user@example.com");
    }

    @Test
    void executeWrongPasswordThrows() {
        var usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("user@example.com");
        usuario.setPassword("hashed");

        when(usuarioRepository.findByEmail("user@example.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matchesPassword("Secret123", "hashed")).thenReturn(false);

        var useCase = new LoginUseCase(usuarioRepository, tokenProvider, passwordEncoder);
        assertThrows(CredentialsInvalidException.class, () -> useCase.execute(new LoginRequest("user@example.com", "Secret123")));
    }

    @Test
    void executeUserNotFoundThrows() {
        when(usuarioRepository.findByEmail("no@exist.com")).thenReturn(Optional.empty());
        var useCase = new LoginUseCase(usuarioRepository, tokenProvider, passwordEncoder);
        assertThrows(UserNotFoundException.class, () -> useCase.execute(new LoginRequest("no@exist.com", "Secret123")));
    }
}
