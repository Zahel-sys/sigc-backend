package com.sigc.backend.domain.service.usecase.auth;

import com.sigc.backend.domain.exception.CredentialsInvalidException;
import com.sigc.backend.domain.exception.UserNotFoundException;
import com.sigc.backend.domain.port.IUsuarioRepository;
import com.sigc.backend.domain.service.validator.CredentialValidator;
import com.sigc.backend.infrastructure.security.jwt.ITokenProvider;
import com.sigc.backend.infrastructure.security.password.IPasswordEncoder;

/**
 * Use Case: Login
 * 
 * Responsabilidad única: Autenticar un usuario.
 * 
 * Flujo:
 * 1. Validar credenciales (email + password)
 * 2. Buscar usuario por email
 * 3. Verificar contraseña
 * 4. Generar token JWT
 * 5. Retornar token
 * 
 * Lógica pura:
 * - Sin dependencias directas de Spring
 * - Sin HTTP
 * - Solo orquesta validadores y puertos
 * 
 * Principo aplicados:
 * - SRP: Solo autentica
 * - DIP: Depende de puertos e interfaces
 */
public class LoginUseCase {
    
    private final IUsuarioRepository usuarioRepository;
    private final ITokenProvider tokenProvider;
    private final IPasswordEncoder passwordEncoder;
    
    public LoginUseCase(IUsuarioRepository usuarioRepository, 
                        ITokenProvider tokenProvider,
                        IPasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * Ejecuta el caso de uso de login.
     * 
     * @param request Credenciales de entrada
     * @return Token JWT si es válido
     * @throws CredentialsInvalidException si las credenciales son inválidas
     * @throws UserNotFoundException si el usuario no existe
     */
    public LoginResponse execute(LoginRequest request) {
        // PASO 1: Validar credenciales de entrada
        CredentialValidator.ValidationResult validationResult = 
            CredentialValidator.validateLoginCredentials(
                request.getEmail(),
                request.getPassword()
            );
        
        if (!validationResult.isValid()) {
            throw new CredentialsInvalidException(validationResult.getErrorMessage());
        }
        
        // PASO 2: Buscar usuario por email
        var usuario = usuarioRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new UserNotFoundException("Usuario con email " + request.getEmail() + " no encontrado"));
        
        // PASO 3: Verificar contraseña
        if (!passwordEncoder.matchesPassword(request.getPassword(), usuario.getPassword())) {
            throw new CredentialsInvalidException("Contraseña incorrecta");
        }
        
        // PASO 4: Generar token JWT
        String token = tokenProvider.generateToken(usuario.getId(), usuario.getEmail(), usuario.getRole());
        
        // PASO 5: Retornar respuesta
        return new LoginResponse(
            usuario.getId(),
            usuario.getEmail(),
            token,
            usuario.getRole()
        );
    }
}
