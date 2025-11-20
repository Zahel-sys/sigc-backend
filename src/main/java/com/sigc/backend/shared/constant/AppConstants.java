package com.sigc.backend.shared.constant;

/**
 * Constantes globales de la aplicación.
 * 
 * Beneficios:
 * - Centraliza valores constantes
 * - Facilita cambios en un único lugar
 * - Evita magic numbers/strings
 * - Reutilizable en múltiples capas
 */
public class AppConstants {
    
    // Configuración de JWT
    public static final long JWT_EXPIRATION_TIME = 86400000; // 1 día en milisegundos
    public static final String JWT_SECRET_KEY = "supersecreta123456supersecreta123456";
    
    // Validación de contraseña
    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final int PASSWORD_MAX_LENGTH = 128;
    public static final boolean PASSWORD_REQUIRE_UPPERCASE = true;
    public static final boolean PASSWORD_REQUIRE_LOWERCASE = true;
    public static final boolean PASSWORD_REQUIRE_DIGITS = true;
    public static final boolean PASSWORD_REQUIRE_SPECIAL = false;
    
    // Validación de email
    public static final int EMAIL_MAX_LENGTH = 255;
    
    // Validación de nombre
    public static final int NAME_MIN_LENGTH = 3;
    public static final int NAME_MAX_LENGTH = 100;
    
    // Paginación
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;
    public static final int MIN_PAGE_SIZE = 1;
    
    // Rutas de archivos
    public static final String UPLOAD_DIR = "uploads/";
    public static final String UPLOAD_IMAGES_DIR = "uploads/images/";
    public static final String UPLOAD_SPECIALTIES_DIR = "uploads/images/especialidades/";
    
    // Tamaño máximo de archivo
    public static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB
    
    // Formatos permitidos
    public static final String[] ALLOWED_IMAGE_FORMATS = {"jpg", "jpeg", "png", "gif"};
    
    // Roles
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_DOCTOR = "DOCTOR";
    public static final String ROLE_PATIENT = "PACIENTE";
    
    // Estados de cita
    public static final String APPOINTMENT_STATUS_PENDING = "PENDIENTE";
    public static final String APPOINTMENT_STATUS_CONFIRMED = "CONFIRMADA";
    public static final String APPOINTMENT_STATUS_CANCELLED = "CANCELADA";
    public static final String APPOINTMENT_STATUS_COMPLETED = "COMPLETADA";
    
    private AppConstants() {
        throw new AssertionError("No se puede instanciar AppConstants");
    }
}
