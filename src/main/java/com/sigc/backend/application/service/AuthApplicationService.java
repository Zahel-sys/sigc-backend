package com.sigc.backend.application.service;

import com.sigc.backend.domain.exception.DomainException;
import com.sigc.backend.domain.port.IUsuarioRepository;
import com.sigc.backend.domain.service.usecase.auth.ChangePasswordUseCase;
import com.sigc.backend.domain.service.usecase.auth.LoginRequest;
import com.sigc.backend.domain.service.usecase.auth.LoginResponse;
import com.sigc.backend.domain.service.usecase.auth.LoginUseCase;
import com.sigc.backend.domain.service.usecase.auth.RegisterUseCase;
import com.sigc.backend.infrastructure.security.jwt.ITokenProvider;
import com.sigc.backend.infrastructure.security.password.IPasswordEncoder;

/**
 * Application Service: Autenticación
 * 
 * Responsabilidad única: Orquestar casos de uso de autenticación.
 * 
 * Orquesta:
 * - LoginUseCase
 * - RegisterUseCase
 * - ChangePasswordUseCase
 * 
 * Principios aplicados:
 * - SRP: Solo gestiona autenticación
 * - DIP: Depende de interfaces/puertos
 * - MVC: Separa lógica de negocio (domain) de HTTP (controller)
 */
public class AuthApplicationService {
    
    private final IUsuarioRepository usuarioRepository;
    private final ITokenProvider tokenProvider;
    private final IPasswordEncoder passwordEncoder;
    
    public AuthApplicationService(IUsuarioRepository usuarioRepository,
                                   ITokenProvider tokenProvider,
                                   IPasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * Ejecuta el proceso de login.
     * 
     * @param request Credenciales de login (email + password)
     * @return Token JWT si es exitoso
     * @throws DomainException si las credenciales son inválidas
     */
    public LoginResponse login(LoginRequest request) {
        var loginUseCase = new LoginUseCase(usuarioRepository, tokenProvider, passwordEncoder);
        return loginUseCase.execute(request);
    }
    
    /**
     * Ejecuta el proceso de registro.
     * 
     * @param request Datos de registro (email + password)
     * @return Confirmación de registro
     * @throws DomainException si el email ya está registrado
     */
    public LoginResponse register(LoginRequest request) {
        var registerUseCase = new RegisterUseCase(usuarioRepository, passwordEncoder);
        return registerUseCase.execute(request);
    }
    
    /**
     * Ejecuta el proceso de cambio de contraseña.
     * 
     * @param changePasswordRequest Datos del cambio
     * @return Confirmación de cambio exitoso
     * @throws DomainException si la contraseña actual es incorrecta
     */
    public ChangePasswordUseCase.ChangePasswordResponse changePassword(
            ChangePasswordUseCase.ChangePasswordRequest changePasswordRequest) {
        var changePasswordUseCase = new ChangePasswordUseCase(usuarioRepository, passwordEncoder);
        return changePasswordUseCase.execute(changePasswordRequest);
    }
}
