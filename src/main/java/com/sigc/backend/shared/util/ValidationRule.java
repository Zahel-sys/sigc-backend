package com.sigc.backend.shared.util;

/**
 * Interfaz para reglas de validación composables.
 * 
 * Patrón: Strategy + Chain of Responsibility
 * 
 * Beneficios:
 * - Cada regla tiene 1 responsabilidad (SRP)
 * - Reglas reutilizables
 * - Fácil de extender con nuevas reglas
 * - Fácil de testear
 * 
 * Ejemplo:
 * List<ValidationRule> rules = Arrays.asList(
 *     new NotEmptyRule(),
 *     new MinLengthRule(8),
 *     new MaxLengthRule(128),
 *     new PatternRule("^[A-Za-z0-9]+$")
 * );
 */
public interface ValidationRule<T> {
    
    /**
     * Valida si el valor cumple la regla.
     * 
     * @param value Valor a validar
     * @return true si es válido, false si no
     */
    boolean isValid(T value);
    
    /**
     * Obtiene el mensaje de error si la validación falla.
     * 
     * @return Mensaje de error descriptivo
     */
    String getErrorMessage();
}
