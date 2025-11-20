package com.sigc.backend.shared.util;

import java.util.regex.Pattern;

/**
 * Regla de validación: Patrón regex.
 * 
 * Reutilizable para cualquier validación con expresiones regulares.
 */
public class PatternRule implements ValidationRule<String> {
    
    private final Pattern pattern;
    private final String fieldName;
    private final String patternDescription;
    
    public PatternRule(String regex) {
        this(regex, "Campo", "el patrón especificado");
    }
    
    public PatternRule(String regex, String fieldName) {
        this(regex, fieldName, "el patrón especificado");
    }
    
    public PatternRule(String regex, String fieldName, String patternDescription) {
        this.pattern = Pattern.compile(regex);
        this.fieldName = fieldName;
        this.patternDescription = patternDescription;
    }
    
    @Override
    public boolean isValid(String value) {
        return value != null && pattern.matcher(value).matches();
    }
    
    @Override
    public String getErrorMessage() {
        return fieldName + " debe cumplir " + patternDescription;
    }
}
