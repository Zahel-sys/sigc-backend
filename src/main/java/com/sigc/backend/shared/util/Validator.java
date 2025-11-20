package com.sigc.backend.shared.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Validador composable: ejecuta múltiples reglas.
 * 
 * Patrón: Composite
 * 
 * Ejemplo:
 * Validator<String> validator = new Validator<String>()
 *     .addRule(new NotEmptyRule("Contraseña"))
 *     .addRule(new MinLengthRule(8, "Contraseña"))
 *     .addRule(new MaxLengthRule(128, "Contraseña"))
 *     .addRule(new PatternRule("^[A-Za-z0-9!@#$%^&*]+$", "Contraseña"));
 * 
 * ValidationResult result = validator.validate(password);
 */
public class Validator<T> {
    
    private final List<ValidationRule<T>> rules = new ArrayList<>();
    
    /**
     * Añade una regla de validación.
     * 
     * @param rule Regla a añadir
     * @return this (para encadenamiento)
     */
    public Validator<T> addRule(ValidationRule<T> rule) {
        rules.add(rule);
        return this;
    }
    
    /**
     * Valida un valor contra todas las reglas añadidas.
     * 
     * @param value Valor a validar
     * @return ValidationResult con resultado y mensajes de error
     */
    public ValidationResult validate(T value) {
        List<String> errors = new ArrayList<>();
        
        for (ValidationRule<T> rule : rules) {
            if (!rule.isValid(value)) {
                errors.add(rule.getErrorMessage());
            }
        }
        
        return new ValidationResult(errors.isEmpty(), errors);
    }
    
    /**
     * Resultado de validación.
     */
    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;
        
        public ValidationResult(boolean valid, List<String> errors) {
            this.valid = valid;
            this.errors = errors;
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public List<String> getErrors() {
            return errors;
        }
        
        public String getErrorMessage() {
            return String.join(", ", errors);
        }
    }
}
