package com.sigc.backend.domain.service.validator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Validador de cita a nivel de dominio.
 * 
 * Responsabilidad única: Validar citas según reglas de negocio.
 * 
 * Reglas de negocio:
 * 1. La fecha no puede ser en el pasado
 * 2. La hora debe estar entre 08:00 y 20:00
 * 3. La hora debe tener 30 minutos (ej: 08:00, 08:30, 09:00)
 * 4. Debe haber descripción (aunque sea breve)
 * 5. El doctor debe existir
 * 
 * Ventajas:
 * - Lógica de negocio pura (sin Spring, sin HTTP)
 * - Reutilizable en múltiples casos de uso
 * - Fácil de testear
 * - Centraliza reglas de negocio
 * 
 * Aplica SRP: Solo valida citas
 * Aplica OCP: Nuevas reglas se añaden fácilmente
 */
public class AppointmentValidator {
    
    private static final LocalTime APPOINTMENT_START_HOUR = LocalTime.of(8, 0);
    private static final LocalTime APPOINTMENT_END_HOUR = LocalTime.of(20, 0);
    private static final int APPOINTMENT_SLOT_MINUTES = 30;
    
    /**
     * Valida una cita en su totalidad.
     * 
     * @param appointmentDate Fecha y hora de la cita
     * @param description Descripción de la cita
     * @param doctorId ID del doctor (para verificar existencia)
     * @return Lista de errores (vacía si es válida)
     */
    public static List<String> validate(LocalDateTime appointmentDate, String description, Long doctorId) {
        List<String> errors = new ArrayList<>();
        
        // Validación 1: Fecha no puede ser en el pasado
        if (appointmentDate != null && appointmentDate.isBefore(LocalDateTime.now())) {
            errors.add("La fecha de la cita no puede ser en el pasado");
        }
        
        // Validación 2: Hora debe estar en rango permitido
        if (appointmentDate != null) {
            LocalTime time = appointmentDate.toLocalTime();
            if (time.isBefore(APPOINTMENT_START_HOUR) || time.isAfter(APPOINTMENT_END_HOUR)) {
                errors.add("La cita debe estar entre las " + APPOINTMENT_START_HOUR + " y " + APPOINTMENT_END_HOUR);
            }
        }
        
        // Validación 3: Minutos deben ser 0 o 30
        if (appointmentDate != null) {
            int minutes = appointmentDate.getMinute();
            if (minutes % APPOINTMENT_SLOT_MINUTES != 0) {
                errors.add("La cita debe estar en slots de 30 minutos (08:00, 08:30, 09:00, etc)");
            }
        }
        
        // Validación 4: Descripción no puede estar vacía
        if (description == null || description.trim().isEmpty()) {
            errors.add("La descripción de la cita no puede estar vacía");
        }
        
        // Validación 5: Descripción no puede ser muy larga
        if (description != null && description.length() > 500) {
            errors.add("La descripción no puede exceder 500 caracteres");
        }
        
        // Validación 6: Doctor debe existir (verificación en persistencia)
        if (doctorId == null || doctorId <= 0) {
            errors.add("El doctor es requerido");
        }
        
        return errors;
    }
    
    /**
     * Valida solo la fecha de la cita.
     * 
     * @param appointmentDate Fecha y hora de la cita
     * @return Lista de errores (vacía si es válida)
     */
    public static List<String> validateDate(LocalDateTime appointmentDate) {
        List<String> errors = new ArrayList<>();
        
        if (appointmentDate == null) {
            errors.add("La fecha de la cita es requerida");
            return errors;
        }
        
        // Validación: Fecha no puede ser en el pasado
        if (appointmentDate.isBefore(LocalDateTime.now())) {
            errors.add("La fecha de la cita no puede ser en el pasado");
        }
        
        // Validación: Hora debe estar en rango permitido
        LocalTime time = appointmentDate.toLocalTime();
        if (time.isBefore(APPOINTMENT_START_HOUR) || time.isAfter(APPOINTMENT_END_HOUR)) {
            errors.add("La cita debe estar entre las " + APPOINTMENT_START_HOUR + " y " + APPOINTMENT_END_HOUR);
        }
        
        // Validación: Minutos deben ser 0 o 30
        int minutes = appointmentDate.getMinute();
        if (minutes % APPOINTMENT_SLOT_MINUTES != 0) {
            errors.add("La cita debe estar en slots de 30 minutos");
        }
        
        return errors;
    }
    
    /**
     * Valida solo la descripción de la cita.
     * 
     * @param description Descripción de la cita
     * @return Lista de errores (vacía si es válida)
     */
    public static List<String> validateDescription(String description) {
        List<String> errors = new ArrayList<>();
        
        if (description == null || description.trim().isEmpty()) {
            errors.add("La descripción de la cita no puede estar vacía");
        }
        
        if (description != null && description.length() > 500) {
            errors.add("La descripción no puede exceder 500 caracteres");
        }
        
        return errors;
    }
    
    /**
     * Valida solo el doctor.
     * 
     * @param doctorId ID del doctor
     * @return Lista de errores (vacía si es válida)
     */
    public static List<String> validateDoctor(Long doctorId) {
        List<String> errors = new ArrayList<>();
        
        if (doctorId == null || doctorId <= 0) {
            errors.add("El doctor es requerido y debe ser válido");
        }
        
        return errors;
    }
    
    /**
     * Verifica si la validación fue exitosa.
     * 
     * @param errors Lista de errores
     * @return true si no hay errores, false si hay
     */
    public static boolean isValid(List<String> errors) {
        return errors == null || errors.isEmpty();
    }
}
