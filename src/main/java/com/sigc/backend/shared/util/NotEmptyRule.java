package com.sigc.backend.shared.util;

/**
 * Regla de validación: El valor no puede estar vacío.
 * 
 * Reutilizable para: Strings, Collections, etc.
 */
public class NotEmptyRule implements ValidationRule<String> {
    
    private final String fieldName;
    
    public NotEmptyRule() {
        this.fieldName = "Campo";
    }
    
    public NotEmptyRule(String fieldName) {
        this.fieldName = fieldName;
    }
    
    @Override
    public boolean isValid(String value) {
        return value != null && !value.trim().isEmpty();
    }
    
    @Override
    public String getErrorMessage() {
        return fieldName + " no puede estar vacío";
    }
}
