package com.sigc.backend.shared.util;

/**
 * Regla de validación: Longitud mínima.
 * 
 * Reutilizable para cualquier String.
 */
public class MinLengthRule implements ValidationRule<String> {
    
    private final int minLength;
    private final String fieldName;
    
    public MinLengthRule(int minLength) {
        this(minLength, "Campo");
    }
    
    public MinLengthRule(int minLength, String fieldName) {
        this.minLength = minLength;
        this.fieldName = fieldName;
    }
    
    @Override
    public boolean isValid(String value) {
        return value != null && value.length() >= minLength;
    }
    
    @Override
    public String getErrorMessage() {
        return fieldName + " debe tener al menos " + minLength + " caracteres";
    }
}
