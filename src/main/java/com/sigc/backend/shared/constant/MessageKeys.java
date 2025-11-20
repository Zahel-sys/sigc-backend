package com.sigc.backend.shared.constant;

/**
 * Claves de mensajes de la aplicación.
 * 
 * Beneficios:
 * - Centraliza mensajes
 * - Facilita internacionalización (i18n)
 * - Evita magic strings
 * - Reutilizable en múltiples capas
 */
public class MessageKeys {
    
    // Mensajes de autenticación
    public static final String AUTH_LOGIN_SUCCESS = "auth.login.success";
    public static final String AUTH_LOGIN_FAILED = "auth.login.failed";
    public static final String AUTH_REGISTER_SUCCESS = "auth.register.success";
    public static final String AUTH_PASSWORD_CHANGED = "auth.password.changed";
    public static final String AUTH_UNAUTHORIZED = "auth.unauthorized";
    public static final String AUTH_TOKEN_EXPIRED = "auth.token.expired";
    
    // Mensajes de usuario
    public static final String USER_CREATED = "user.created";
    public static final String USER_UPDATED = "user.updated";
    public static final String USER_DELETED = "user.deleted";
    public static final String USER_NOT_FOUND = "user.not.found";
    public static final String USER_EMAIL_EXISTS = "user.email.exists";
    public static final String USER_INVALID_EMAIL = "user.invalid.email";
    public static final String USER_PASSWORD_INVALID = "user.password.invalid";
    public static final String USER_PASSWORD_MISMATCH = "user.password.mismatch";
    
    // Mensajes de cita
    public static final String APPOINTMENT_CREATED = "appointment.created";
    public static final String APPOINTMENT_UPDATED = "appointment.updated";
    public static final String APPOINTMENT_CANCELLED = "appointment.cancelled";
    public static final String APPOINTMENT_NOT_FOUND = "appointment.not.found";
    public static final String APPOINTMENT_INVALID_DATE = "appointment.invalid.date";
    public static final String APPOINTMENT_INVALID_TIME = "appointment.invalid.time";
    public static final String APPOINTMENT_DOCTOR_UNAVAILABLE = "appointment.doctor.unavailable";
    
    // Mensajes de horario
    public static final String SCHEDULE_CREATED = "schedule.created";
    public static final String SCHEDULE_UPDATED = "schedule.updated";
    public static final String SCHEDULE_DELETED = "schedule.deleted";
    public static final String SCHEDULE_NOT_FOUND = "schedule.not.found";
    
    // Mensajes de validación
    public static final String VALIDATION_ERROR = "validation.error";
    public static final String INVALID_INPUT = "validation.invalid.input";
    public static final String MISSING_FIELD = "validation.missing.field";
    public static final String FIELD_TOO_LONG = "validation.field.too.long";
    public static final String FIELD_TOO_SHORT = "validation.field.too.short";
    
    // Mensajes del servidor
    public static final String INTERNAL_ERROR = "server.internal.error";
    public static final String DATABASE_ERROR = "server.database.error";
    public static final String FILE_UPLOAD_ERROR = "server.file.upload.error";
    
    private MessageKeys() {
        throw new AssertionError("No se puede instanciar MessageKeys");
    }
}
