package com.sigc.backend.shared.value;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object: Email
 * 
 * Encapsula la lógica de validación de emails.
 * Beneficios:
 * - Type-safe: no es un String cualquiera
 * - Validación centralizada
 * - Reutilizable en múltiples entidades
 * - Aplica DDD (Domain-Driven Design)
 */
public class Email {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    private final String value;
    
    private Email(String value) {
        this.value = value;
    }
    
    /**
     * Factory method: crea un Email validado.
     * Lanza excepción si el email es inválido.
     * 
     * @param email Email en texto
     * @return Email validado
     * @throws IllegalArgumentException si el email es inválido
     */
    public static Email of(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El email no puede estar vacío");
        }
        
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new IllegalArgumentException("El email no tiene un formato válido: " + email);
        }
        
        return new Email(email.trim());
    }
    
    /**
     * Obtiene el valor del email.
     */
    public String getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return Objects.equals(value, email.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
