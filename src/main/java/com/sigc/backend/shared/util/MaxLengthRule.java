package com.sigc.backend.shared.util;

/**
 * Regla de validación: Longitud máxima.
 * 
 * Reutilizable para cualquier String.
 */
public class MaxLengthRule implements ValidationRule<String> {
    
    private final int maxLength;
    private final String fieldName;
    
    public MaxLengthRule(int maxLength) {
        this(maxLength, "Campo");
    }
    
    public MaxLengthRule(int maxLength, String fieldName) {
        this.maxLength = maxLength;
        this.fieldName = fieldName;
    }
    
    @Override
    public boolean isValid(String value) {
        return value == null || value.length() <= maxLength;
    }
    
    @Override
    public String getErrorMessage() {
        return fieldName + " no puede exceder " + maxLength + " caracteres";
    }
}
