package com.sigc.backend.domain.service.validator;

import com.sigc.backend.shared.value.Email;
import com.sigc.backend.shared.util.*;

/**
 * Validador de credenciales a nivel de dominio.
 * 
 * Responsabilidad única: Validar credenciales (email + password) según reglas de negocio.
 * 
 * Reglas de negocio:
 * 1. Email debe ser válido
 * 2. Contraseña no puede estar vacía
 * 3. Contraseña debe cumplir requisitos de fortaleza
 * 
 * Ventajas:
 * - Lógica de negocio pura (sin Spring, sin HTTP)
 * - Reutilizable en múltiples casos de uso (login, registro)
 * - Fácil de testear
 * - Centraliza reglas de negocio de autenticación
 * 
 * Aplica SRP: Solo valida credenciales
 * Aplica OCP: Nuevas reglas se añaden fácilmente
 */
public class CredentialValidator {
    
    /**
     * Valida credenciales de login.
     * 
     * @param email Email del usuario
     * @param password Contraseña del usuario
     * @return Objeto con estado de validación y mensajes de error
     */
    public static ValidationResult validateLoginCredentials(String email, String password) {
        ValidationResult result = new ValidationResult();
        
        // Validación 1: Email válido
        try {
            Email.of(email);
        } catch (IllegalArgumentException e) {
            result.addError("Email: " + e.getMessage());
        }
        
        // Validación 2: Contraseña no vacía
        if (password == null || password.trim().isEmpty()) {
            result.addError("La contraseña no puede estar vacía");
        }
        
        return result;
    }
    
    /**
     * Valida credenciales de registro.
     * 
     * @param email Email del usuario
     * @param password Contraseña del usuario
     * @param confirmPassword Confirmación de contraseña
     * @return Objeto con estado de validación y mensajes de error
     */
    public static ValidationResult validateRegistrationCredentials(String email, String password, String confirmPassword) {
        ValidationResult result = new ValidationResult();
        
        // Validación 1: Email válido
        try {
            Email.of(email);
        } catch (IllegalArgumentException e) {
            result.addError("Email: " + e.getMessage());
        }
        
        // Validación 2: Contraseña válida (cumple requisitos)
        Validator.ValidationResult passwordResult = PasswordValidator.validate(password);
        if (!passwordResult.isValid()) {
            result.addError("Contraseña: " + passwordResult.getErrorMessage());
        }
        
        // Validación 3: Confirmación coincide
        if (!PasswordValidator.match(password, confirmPassword)) {
            result.addError("Las contraseñas no coinciden");
        }
        
        return result;
    }
    
    /**
     * Valida solo el email.
     * 
     * @param email Email a validar
     * @return Objeto con estado de validación y mensaje de error
     */
    public static ValidationResult validateEmail(String email) {
        ValidationResult result = new ValidationResult();
        
        try {
            Email.of(email);
        } catch (IllegalArgumentException e) {
            result.addError(e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Valida solo la contraseña.
     * 
     * @param password Contraseña a validar
     * @return Objeto con estado de validación y mensaje de error
     */
    public static ValidationResult validatePassword(String password) {
        ValidationResult result = new ValidationResult();
        
        Validator.ValidationResult passwordResult = PasswordValidator.validate(password);
        if (!passwordResult.isValid()) {
            result.addError(passwordResult.getErrorMessage());
        }
        
        return result;
    }
    
    /**
     * Objeto de resultado de validación.
     */
    public static class ValidationResult {
        private final java.util.List<String> errors = new java.util.ArrayList<>();
        
        public void addError(String error) {
            errors.add(error);
        }
        
        public boolean isValid() {
            return errors.isEmpty();
        }
        
        public java.util.List<String> getErrors() {
            return errors;
        }
        
        public String getErrorMessage() {
            return String.join(", ", errors);
        }
    }
}
