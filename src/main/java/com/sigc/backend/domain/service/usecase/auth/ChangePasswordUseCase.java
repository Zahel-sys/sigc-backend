package com.sigc.backend.domain.service.usecase.auth;

import com.sigc.backend.domain.exception.CredentialsInvalidException;
import com.sigc.backend.domain.exception.UserNotFoundException;
import com.sigc.backend.domain.port.IUsuarioRepository;
import com.sigc.backend.domain.service.validator.PasswordValidator;
import com.sigc.backend.infrastructure.security.password.IPasswordEncoder;
import com.sigc.backend.shared.util.Validator;

/**
 * Use Case: ChangePassword
 * 
 * Responsabilidad única: Cambiar la contraseña de un usuario.
 * 
 * Flujo:
 * 1. Buscar usuario por ID
 * 2. Validar contraseña actual
 * 3. Validar nueva contraseña (requisitos + confirmación)
 * 4. Encriptar nueva contraseña
 * 5. Actualizar usuario
 * 6. Retornar confirmación
 * 
 * Lógica pura:
 * - Sin dependencias directas de Spring
 * - Sin HTTP
 * - Solo orquesta validadores y puertos
 * 
 * Principios aplicados:
 * - SRP: Solo cambia contraseña
 * - DIP: Depende de puertos e interfaces
 */
public class ChangePasswordUseCase {
    
    private final IUsuarioRepository usuarioRepository;
    private final IPasswordEncoder passwordEncoder;
    
    public ChangePasswordUseCase(IUsuarioRepository usuarioRepository,
                                 IPasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * Solicitud de cambio de contraseña.
     */
    public static class ChangePasswordRequest {
        private final Long userId;
        private final String currentPassword;
        private final String newPassword;
        private final String confirmPassword;
        
        public ChangePasswordRequest(Long userId, String currentPassword, 
                                    String newPassword, String confirmPassword) {
            this.userId = userId;
            this.currentPassword = currentPassword;
            this.newPassword = newPassword;
            this.confirmPassword = confirmPassword;
        }
        
        public Long getUserId() { return userId; }
        public String getCurrentPassword() { return currentPassword; }
        public String getNewPassword() { return newPassword; }
        public String getConfirmPassword() { return confirmPassword; }
    }
    
    /**
     * Respuesta de cambio de contraseña.
     */
    public static class ChangePasswordResponse {
        private final String message;
        private final boolean success;
        
        public ChangePasswordResponse(String message, boolean success) {
            this.message = message;
            this.success = success;
        }
        
        public String getMessage() { return message; }
        public boolean isSuccess() { return success; }
    }
    
    /**
     * Ejecuta el caso de uso de cambio de contraseña.
     * 
     * @param request Datos del cambio de contraseña
     * @return Confirmación de cambio exitoso
     * @throws UserNotFoundException si el usuario no existe
     * @throws CredentialsInvalidException si la contraseña actual es incorrecta
     */
    public ChangePasswordResponse execute(ChangePasswordRequest request) {
        // PASO 1: Buscar usuario por ID
        var usuario = usuarioRepository.findById(request.getUserId())
            .orElseThrow(() -> new UserNotFoundException("Usuario con ID " + request.getUserId() + " no encontrado"));
        
        // PASO 2: Validar contraseña actual
        if (!passwordEncoder.matchesPassword(request.getCurrentPassword(), getUsuarioPassword(usuario))) {
            throw new CredentialsInvalidException("Contraseña actual incorrecta");
        }
        
        // PASO 3: Validar nueva contraseña (requisitos + confirmación)
        Validator.ValidationResult result = PasswordValidator.validatePasswordChange(
            request.getNewPassword(),
            request.getConfirmPassword()
        );
        
        if (!result.isValid()) {
            throw new IllegalArgumentException(result.getErrorMessage());
        }
        
        // PASO 4: Encriptar nueva contraseña
        String encryptedPassword = passwordEncoder.encodePassword(request.getNewPassword());
        
        // PASO 5: Actualizar usuario
        updateUsuarioPassword(usuario, encryptedPassword);
        usuarioRepository.save(usuario);
        
        // PASO 6: Retornar confirmación
        return new ChangePasswordResponse("Contraseña actualizada exitosamente", true);
    }
    
    /**
     * Método auxiliar para obtener la contraseña del usuario.
     * Será reemplazado cuando el modelo Usuario sea implementado.
     */
    private String getUsuarioPassword(Object usuario) {
        // PLACEHOLDER: Será implementado cuando Usuario sea creado
        return "";
    }
    
    /**
     * Método auxiliar para actualizar la contraseña del usuario.
     * Será reemplazado cuando el modelo Usuario sea implementado.
     */
    private void updateUsuarioPassword(Object usuario, String encryptedPassword) {
        // PLACEHOLDER: Será implementado cuando Usuario sea creado
    }
}
