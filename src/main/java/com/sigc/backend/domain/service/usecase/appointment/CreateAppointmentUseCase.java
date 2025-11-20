package com.sigc.backend.domain.service.usecase.appointment;

import com.sigc.backend.domain.exception.AppointmentInvalidException;
import com.sigc.backend.domain.model.Cita;
import com.sigc.backend.domain.port.ICitaRepository;
import com.sigc.backend.domain.service.validator.AppointmentValidator;

/**
 * Use Case: CreateAppointment
 * 
 * Responsabilidad única: Crear una nueva cita médica.
 * 
 * Flujo:
 * 1. Validar datos de la cita
 * 2. Validar fecha
 * 3. Validar descripción
 * 4. Validar doctor
 * 5. Crear cita en base de datos
 * 6. Retornar cita creada
 * 
 * Lógica pura:
 * - Sin dependencias directas de Spring
 * - Sin HTTP
 * - Solo orquesta validadores y puertos
 * 
 * Principios aplicados:
 * - SRP: Solo crea citas
 * - DIP: Depende de puertos e interfaces
 */
public class CreateAppointmentUseCase {
    
    private final ICitaRepository citaRepository;
    
    public CreateAppointmentUseCase(ICitaRepository citaRepository) {
        this.citaRepository = citaRepository;
    }
    
    /**
     * Ejecuta el caso de uso de creación de cita.
     * 
     * @param request Datos de la nueva cita
     * @return Cita creada
     * @throws AppointmentInvalidException si los datos de la cita son inválidos
     */
    public CreateAppointmentResponse execute(CreateAppointmentRequest request) {
        // PASO 1: Validar datos de la cita (objeto completo)
        var errores = AppointmentValidator.validate(
                request.getDate(), 
                request.getDescription(), 
                request.getDoctorId());
        
        if (!AppointmentValidator.isValid(errores)) {
            throw new AppointmentInvalidException("Datos de cita inválidos: " + String.join(", ", errores));
        }
        
        // PASO 2: Validar fecha específicamente
        var erroresFecha = AppointmentValidator.validateDate(request.getDate());
        if (!AppointmentValidator.isValid(erroresFecha)) {
            throw new AppointmentInvalidException("Fecha de cita inválida: " + String.join(", ", erroresFecha));
        }
        
        // PASO 3: Validar descripción
        var erroresDesc = AppointmentValidator.validateDescription(request.getDescription());
        if (!AppointmentValidator.isValid(erroresDesc)) {
            throw new AppointmentInvalidException("Descripción inválida: " + String.join(", ", erroresDesc));
        }
        
        // PASO 4: Validar doctor
        var erroresDoctor = AppointmentValidator.validateDoctor(request.getDoctorId());
        if (!AppointmentValidator.isValid(erroresDoctor)) {
            throw new AppointmentInvalidException("Doctor inválido: " + String.join(", ", erroresDoctor));
        }
        
        // PASO 5: Crear cita en base de datos
        var nuevaCita = crearCita(request);
        citaRepository.save(nuevaCita);
        
        // PASO 6: Retornar cita creada
        return new CreateAppointmentResponse(
            obtenerCitaId(nuevaCita),
            request.getDate(),
            request.getDescription(),
            request.getDoctorId(),
            request.getUsuarioId(),
            "PROGRAMADA"
        );
    }
    
    /**
     * Método auxiliar para crear objeto Cita.
     * Este método será reemplazado por una factory o builder
     * cuando el modelo Cita sea implementado.
     */
    private Cita crearCita(CreateAppointmentRequest request) {
        // PLACEHOLDER: Será implementado completamente en PASO 6
        // Por ahora retorna Cita básica
        return new Cita(
            request.getUsuarioId(),
            request.getDoctorId(),
            request.getDate(),
            request.getDescription()
        );
    }
    
    /**
     * Método auxiliar para obtener el ID de la cita.
     * Será reemplazado cuando el modelo Cita sea implementado.
     */
    private Long obtenerCitaId(Object cita) {
        // PLACEHOLDER: Será implementado cuando Cita sea creado
        if (cita instanceof Cita) {
            return ((Cita) cita).getId();
        }
        return 0L;
    }
}
