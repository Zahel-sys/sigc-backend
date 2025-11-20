package com.sigc.backend.shared.constant;

/**
 * Códigos de error estándar para la aplicación.
 * 
 * Beneficios:
 * - Centraliza códigos de error
 * - Facilita mantenimiento
 * - Evita magic strings
 * - Reutilizable en múltiples capas
 */
public class ErrorCodes {
    
    // Errores de autenticación
    public static final String AUTH_INVALID_CREDENTIALS = "AUTH_001";
    public static final String AUTH_USER_NOT_FOUND = "AUTH_002";
    public static final String AUTH_TOKEN_EXPIRED = "AUTH_003";
    public static final String AUTH_TOKEN_INVALID = "AUTH_004";
    public static final String AUTH_UNAUTHORIZED = "AUTH_005";
    
    // Errores de usuario
    public static final String USER_NOT_FOUND = "USER_001";
    public static final String USER_EMAIL_ALREADY_EXISTS = "USER_002";
    public static final String USER_INVALID_EMAIL = "USER_003";
    public static final String USER_INVALID_PASSWORD = "USER_004";
    public static final String USER_PASSWORD_MISMATCH = "USER_005";
    public static final String USER_CURRENT_PASSWORD_WRONG = "USER_006";
    
    // Errores de cita
    public static final String APPOINTMENT_NOT_FOUND = "APPOINTMENT_001";
    public static final String APPOINTMENT_INVALID_DATE = "APPOINTMENT_002";
    public static final String APPOINTMENT_INVALID_TIME = "APPOINTMENT_003";
    public static final String APPOINTMENT_DOCTOR_UNAVAILABLE = "APPOINTMENT_004";
    public static final String APPOINTMENT_DOCTOR_NOT_FOUND = "APPOINTMENT_005";
    
    // Errores de horario
    public static final String SCHEDULE_NOT_FOUND = "SCHEDULE_001";
    public static final String SCHEDULE_INVALID = "SCHEDULE_002";
    public static final String SCHEDULE_DOCTOR_NOT_AVAILABLE = "SCHEDULE_003";
    
    // Errores de validación general
    public static final String VALIDATION_ERROR = "VALIDATION_001";
    public static final String INVALID_INPUT = "VALIDATION_002";
    public static final String MISSING_FIELD = "VALIDATION_003";
    
    // Errores del servidor
    public static final String INTERNAL_ERROR = "SERVER_001";
    public static final String DATABASE_ERROR = "SERVER_002";
    public static final String FILE_UPLOAD_ERROR = "SERVER_003";
    
    private ErrorCodes() {
        throw new AssertionError("No se puede instanciar ErrorCodes");
    }
}
