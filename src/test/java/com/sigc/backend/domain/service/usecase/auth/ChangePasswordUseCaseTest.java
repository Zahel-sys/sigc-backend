package com.sigc.backend.domain.service.usecase.auth;

import com.sigc.backend.domain.exception.CredentialsInvalidException;
import com.sigc.backend.domain.exception.UserNotFoundException;
import com.sigc.backend.domain.model.Usuario;
import com.sigc.backend.domain.port.IUsuarioRepository;
import com.sigc.backend.infrastructure.security.password.IPasswordEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ChangePasswordUseCaseTest {

    private IUsuarioRepository usuarioRepository;
    private IPasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        usuarioRepository = Mockito.mock(IUsuarioRepository.class);
        passwordEncoder = Mockito.mock(IPasswordEncoder.class);
    }

    @Test
    void userNotFoundThrows() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());
        var useCase = new ChangePasswordUseCase(usuarioRepository, passwordEncoder);
        var req = new ChangePasswordUseCase.ChangePasswordRequest(1L, "old", "Newpass1", "Newpass1");
        assertThrows(UserNotFoundException.class, () -> useCase.execute(req));
    }

    @Test
    void wrongCurrentPasswordThrows() {
        var usuario = new Usuario();
        // placeholders in use case mean we only can test the password mismatch branch
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matchesPassword("wrong", "")).thenReturn(false);
        var useCase = new ChangePasswordUseCase(usuarioRepository, passwordEncoder);
        var req = new ChangePasswordUseCase.ChangePasswordRequest(2L, "wrong", "Newpass1", "Newpass1");
        assertThrows(CredentialsInvalidException.class, () -> useCase.execute(req));
    }
}
