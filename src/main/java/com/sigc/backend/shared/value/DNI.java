package com.sigc.backend.shared.value;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object: DNI
 * 
 * Encapsula la lógica de validación de DNI (Documento Nacional de Identidad).
 * Beneficios:
 * - Type-safe: no es un String cualquiera
 * - Validación centralizada
 * - Reutilizable en múltiples entidades
 * - Aplica DDD (Domain-Driven Design)
 */
public class DNI {
    
    private static final Pattern DNI_PATTERN = Pattern.compile(
            "^[0-9]{8,10}$"  // DNI de 8-10 dígitos
    );
    
    private final String value;
    
    private DNI(String value) {
        this.value = value;
    }
    
    /**
     * Factory method: crea un DNI validado.
     * Lanza excepción si el DNI es inválido.
     * 
     * @param dni Número de DNI en texto
     * @return DNI validado
     * @throws IllegalArgumentException si el DNI es inválido
     */
    public static DNI of(String dni) {
        if (dni == null || dni.trim().isEmpty()) {
            throw new IllegalArgumentException("El DNI no puede estar vacío");
        }
        
        if (!DNI_PATTERN.matcher(dni.trim()).matches()) {
            throw new IllegalArgumentException("El DNI no tiene un formato válido: " + dni);
        }
        
        return new DNI(dni.trim());
    }
    
    /**
     * Obtiene el valor del DNI.
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
        DNI dni = (DNI) o;
        return Objects.equals(value, dni.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
