package com.sigc.backend.shared.value;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object: PhoneNumber
 * 
 * Encapsula la lógica de validación de números de teléfono.
 * Beneficios:
 * - Type-safe: no es un String cualquiera
 * - Validación centralizada
 * - Reutilizable en múltiples entidades
 * - Aplica DDD (Domain-Driven Design)
 */
public class PhoneNumber {
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^[0-9+\\-\\s()]{7,20}$"
    );
    
    private final String value;
    
    private PhoneNumber(String value) {
        this.value = value;
    }
    
    /**
     * Factory method: crea un PhoneNumber validado.
     * Lanza excepción si el teléfono es inválido.
     * 
     * @param phone Número de teléfono en texto
     * @return PhoneNumber validado
     * @throws IllegalArgumentException si el teléfono es inválido
     */
    public static PhoneNumber of(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de teléfono no puede estar vacío");
        }
        
        if (!PHONE_PATTERN.matcher(phone.trim()).matches()) {
            throw new IllegalArgumentException("El número de teléfono no tiene un formato válido: " + phone);
        }
        
        return new PhoneNumber(phone.trim());
    }
    
    /**
     * Obtiene el valor del número de teléfono.
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
        PhoneNumber that = (PhoneNumber) o;
        return Objects.equals(value, that.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
