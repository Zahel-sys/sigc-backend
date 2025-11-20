package com.sigc.backend.domain.service.validator;

import com.sigc.backend.shared.util.*;

/**
 * Validador de contraseña a nivel de dominio.
 * 
 * Responsabilidad única: Validar contraseñas según reglas de negocio.
 * 
 * Ventajas:
 * - Lógica de negocio pura (sin Spring, sin HTTP)
 * - Reutilizable en múltiples casos de uso
 * - Fácil de testear
 * - Reglas segregadas en clases individuales
 * 
 * Aplica SRP: Solo valida contraseñas
 * Aplica OCP: Nuevas reglas se añaden sin modificar esta clase
 */
public class PasswordValidator {
    
    /**
     * Valida una contraseña según las reglas de negocio.
     * 
     * @param password Contraseña a validar
     * @return ValidationResult con estado y mensajes de error
     */
    public static Validator.ValidationResult validate(String password) {
        Validator<String> validator = new Validator<String>()
                .addRule(new NotEmptyRule("Contraseña"))
                .addRule(new MinLengthRule(8, "Contraseña"))
                .addRule(new MaxLengthRule(128, "Contraseña"))
                .addRule(new PatternRule(
                        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{8,}$",
                        "Contraseña",
                        "contener mayúscula, minúscula y número"
                ));
        
        return validator.validate(password);
    }
    
    /**
     * Valida si dos contraseñas coinciden.
     * 
     * @param password1 Primera contraseña
     * @param password2 Segunda contraseña
     * @return true si coinciden, false en caso contrario
     */
    public static boolean match(String password1, String password2) {
        if (password1 == null || password2 == null) {
            return false;
        }
        return password1.equals(password2);
    }
    
    /**
     * Valida contraseña actual vs nueva contraseña.
     * 
     * @param currentPassword Contraseña actual
     * @param newPassword Nueva contraseña
     * @return ValidationResult con estado y mensajes de error
     */
    public static Validator.ValidationResult validatePasswordChange(String currentPassword, String newPassword) {
        Validator<String> validator = new Validator<String>()
                .addRule(new NotEmptyRule("Contraseña actual"))
                .addRule(new NotEmptyRule("Contraseña nueva"));
        
        Validator.ValidationResult result = validator.validate(currentPassword + newPassword);
        
        if (!result.isValid()) {
            return result;
        }
        
        // Valida la nueva contraseña
        Validator.ValidationResult newPasswordValidation = validate(newPassword);
        if (!newPasswordValidation.isValid()) {
            return newPasswordValidation;
        }
        
        // Valida que no sean iguales
        if (currentPassword.equals(newPassword)) {
            return new Validator.ValidationResult(false, 
                    java.util.Arrays.asList("La nueva contraseña debe ser diferente a la actual"));
        }
        
        return new Validator.ValidationResult(true, java.util.Collections.emptyList());
    }
    
    /**
     * Valida confirmación de contraseña (contraseña nueva vs confirmación).
     * 
     * @param newPassword Nueva contraseña
     * @param confirmPassword Confirmación de contraseña
     * @return ValidationResult con estado y mensajes de error
     */
    public static Validator.ValidationResult validateConfirmation(String newPassword, String confirmPassword) {
        if (newPassword == null || confirmPassword == null) {
            return new Validator.ValidationResult(false, 
                    java.util.Arrays.asList("Las contraseñas no pueden estar vacías"));
        }
        
        if (!newPassword.equals(confirmPassword)) {
            return new Validator.ValidationResult(false, 
                    java.util.Arrays.asList("Las contraseñas no coinciden"));
        }
        
        return new Validator.ValidationResult(true, java.util.Collections.emptyList());
    }
}
