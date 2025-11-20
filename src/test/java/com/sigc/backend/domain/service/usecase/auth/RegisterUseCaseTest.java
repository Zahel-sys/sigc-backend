package com.sigc.backend.domain.service.usecase.auth;

import com.sigc.backend.domain.exception.EmailAlreadyRegisteredException;
import com.sigc.backend.domain.port.IUsuarioRepository;
import com.sigc.backend.infrastructure.security.password.IPasswordEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RegisterUseCaseTest {

    private IUsuarioRepository usuarioRepository;
    private IPasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        usuarioRepository = Mockito.mock(IUsuarioRepository.class);
        passwordEncoder = Mockito.mock(IPasswordEncoder.class);
    }

    @Test
    void registerSuccess() {
        when(usuarioRepository.existsByEmail("new@user.com")).thenReturn(false);
        when(passwordEncoder.encodePassword("Secret123")).thenReturn("enc");

        var useCase = new RegisterUseCase(usuarioRepository, passwordEncoder);
        var resp = useCase.execute(new LoginRequest("new@user.com", "Secret123"));

        assertNotNull(resp);
        assertEquals("new@user.com", resp.getEmail());
        verify(usuarioRepository, times(1)).save(any());
    }

    @Test
    void registerEmailAlreadyRegisteredThrows() {
        when(usuarioRepository.existsByEmail("taken@user.com")).thenReturn(true);
        var useCase = new RegisterUseCase(usuarioRepository, passwordEncoder);
        assertThrows(EmailAlreadyRegisteredException.class, () -> useCase.execute(new LoginRequest("taken@user.com", "Secret123")));
    }
}
