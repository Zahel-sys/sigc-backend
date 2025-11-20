package com.sigc.backend.domain.service.usecase.auth;

import com.sigc.backend.domain.exception.EmailAlreadyRegisteredException;
import com.sigc.backend.domain.model.Usuario;
import com.sigc.backend.domain.port.IUsuarioRepository;
import com.sigc.backend.domain.service.validator.CredentialValidator;
import com.sigc.backend.infrastructure.security.password.IPasswordEncoder;

/**
 * Use Case: Register
 * 
 * Responsabilidad única: Registrar un nuevo usuario.
 * 
 * Flujo:
 * 1. Validar credenciales de registro
 * 2. Verificar que el email no esté ya registrado
 * 3. Encriptar contraseña
 * 4. Crear usuario en base de datos
 * 5. Retornar confirmación
 * 
 * Lógica pura:
 * - Sin dependencias directas de Spring
 * - Sin HTTP
 * - Solo orquesta validadores y puertos
 * 
 * Principios aplicados:
 * - SRP: Solo registra
 * - DIP: Depende de puertos e interfaces
 */
public class RegisterUseCase {
    
    private final IUsuarioRepository usuarioRepository;
    private final IPasswordEncoder passwordEncoder;
    
    public RegisterUseCase(IUsuarioRepository usuarioRepository,
                           IPasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * Ejecuta el caso de uso de registro.
     * 
     * @param request Datos de registro del nuevo usuario
     * @return Confirmación de registro creado
     * @throws EmailAlreadyRegisteredException si el email ya está registrado
     */
    public LoginResponse execute(LoginRequest request) {
        // PASO 1: Validar credenciales de registro
        CredentialValidator.ValidationResult validationResult = 
            CredentialValidator.validateRegistrationCredentials(
                request.getEmail(),
                request.getPassword(),
                request.getPassword()  // confirmPassword (mismo que password en login simple)
            );
        
        if (!validationResult.isValid()) {
            throw new IllegalArgumentException(validationResult.getErrorMessage());
        }
        
        // PASO 2: Verificar que el email no esté registrado
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyRegisteredException("El email " + request.getEmail() + " ya está registrado");
        }
        
        // PASO 3: Encriptar contraseña
        String encryptedPassword = passwordEncoder.encodePassword(request.getPassword());
        
        // PASO 4: Crear usuario en base de datos
        // Nota: La creación del usuario será delegada al repositorio
        // Este caso de uso solo asegura la lógica de negocio
        var nuevoUsuario = crearUsuario(request.getEmail(), encryptedPassword);
        usuarioRepository.save(nuevoUsuario);
        
        // PASO 5: Retornar confirmación
        // En registro, no devolvemos token inmediatamente
        // El usuario debe hacer login después
        return new LoginResponse(
            0L,  // ID será asignado por la base de datos
            request.getEmail(),
            "",  // Sin token en registro
            "USER"
        );
    }
    
    /**
     * Método auxiliar para crear objeto Usuario.
     * Este método será reemplazado por una factory o builder
     * cuando el modelo Usuario sea implementado.
     */
    private Usuario crearUsuario(String email, String encryptedPassword) {
        // PLACEHOLDER: Será implementado completamente en PASO 6
        // Por ahora retorna Usuario básico
        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setPassword(encryptedPassword);
        usuario.setRole("USER");
        usuario.setActivo(true);
        return usuario;
    }
}
