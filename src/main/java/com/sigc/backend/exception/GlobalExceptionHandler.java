package com.sigc.backend.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones
 * Captura y formatea errores de toda la aplicación
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Maneja errores de validación de campos (@Valid)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String campo = ((FieldError) error).getField();
            String mensaje = error.getDefaultMessage();
            errores.put(campo, mensaje);
        });

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("timestamp", LocalDateTime.now());
        respuesta.put("status", HttpStatus.BAD_REQUEST.value());
        respuesta.put("error", "Errores de validación");
        respuesta.put("errores", errores);

        log.warn("Errores de validación: {}", errores);
        return ResponseEntity.badRequest().body(respuesta);
    }

    /**
     * Maneja emails duplicados (409 Conflict)
     */
    @ExceptionHandler(EmailDuplicadoException.class)
    public ResponseEntity<Map<String, Object>> handleEmailDuplicado(EmailDuplicadoException ex) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("timestamp", LocalDateTime.now());
        respuesta.put("status", HttpStatus.CONFLICT.value());
        respuesta.put("error", "Email duplicado");
        respuesta.put("mensaje", ex.getMessage());

        log.warn("Email duplicado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(respuesta);
    }

    /**
     * Maneja violaciones de integridad de datos (claves duplicadas, restricciones, etc.)
     * Captura errores de MySQL como "Duplicate entry" y los convierte en 409 Conflict
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("timestamp", LocalDateTime.now());
        respuesta.put("status", HttpStatus.CONFLICT.value());
        
        String mensajeError = ex.getMostSpecificCause().getMessage();
        
        // Detectar si es un error de email duplicado
        if (mensajeError != null && mensajeError.toLowerCase().contains("duplicate entry") && 
            mensajeError.toLowerCase().contains("email")) {
            respuesta.put("error", "Email duplicado");
            respuesta.put("mensaje", "El email ya está registrado en el sistema");
            log.warn("Intento de registro con email duplicado: {}", mensajeError);
        } 
        // Detectar si es un error de DNI duplicado
        else if (mensajeError != null && mensajeError.toLowerCase().contains("duplicate entry") && 
                 mensajeError.toLowerCase().contains("dni")) {
            respuesta.put("error", "DNI duplicado");
            respuesta.put("mensaje", "El DNI ya está registrado en el sistema");
            log.warn("Intento de registro con DNI duplicado: {}", mensajeError);
        }
        // Otras violaciones de integridad
        else {
            respuesta.put("error", "Violación de restricción de datos");
            respuesta.put("mensaje", "Los datos proporcionados violan una restricción de la base de datos");
            log.warn("Violación de integridad de datos: {}", mensajeError);
        }

        return ResponseEntity.status(HttpStatus.CONFLICT).body(respuesta);
    }

    /**
     * Maneja errores genéricos no capturados
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericError(Exception ex) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("timestamp", LocalDateTime.now());
        respuesta.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        respuesta.put("error", "Error interno del servidor");
        respuesta.put("mensaje", "Ocurrió un error inesperado. Por favor contacte al administrador.");

        log.error("Error no manejado: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuesta);
    }
}
