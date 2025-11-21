package com.sigc.backend.application.service;

import com.sigc.backend.application.mapper.CitaMapper;
import com.sigc.backend.domain.exception.DomainException;
import com.sigc.backend.domain.model.Cita;
import com.sigc.backend.domain.port.ICitaRepository;
import com.sigc.backend.domain.service.usecase.appointment.CreateAppointmentRequest;
import com.sigc.backend.domain.service.usecase.appointment.CreateAppointmentResponse;
import com.sigc.backend.domain.service.usecase.appointment.CreateAppointmentUseCase;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Application Service: Citas
 * 
 * Responsabilidad única: Orquestar casos de uso de citas.
 * 
 * Orquesta:
 * - CreateAppointmentUseCase
 * - (Futuros: GetAppointmentUseCase, UpdateAppointmentUseCase, CancelAppointmentUseCase)
 * 
 * Principios aplicados:
 * - SRP: Solo gestiona citas
 * - DIP: Depende de interfaces/puertos
 * - MVC: Separa lógica de negocio (domain) de HTTP (controller)
 */
@Service
public class AppointmentApplicationService {
    
    private final ICitaRepository citaRepository;
    
    // Nota: esta clase ahora depende únicamente del puerto `ICitaRepository`.
    
    public AppointmentApplicationService(ICitaRepository citaRepository) {
        this.citaRepository = citaRepository;
    }
    
    /**
     * Ejecuta el proceso de creación de cita.
     * 
     * @param request Datos de la nueva cita
     * @return Cita creada con ID asignado
     * @throws DomainException si los datos de la cita son inválidos
     */
    public CreateAppointmentResponse createAppointment(CreateAppointmentRequest request) {
        var createAppointmentUseCase = new CreateAppointmentUseCase(citaRepository);
        return createAppointmentUseCase.execute(request);
    }

    /**
     * Obtiene todas las citas y las devuelve como DTOs.
     */
    public java.util.List<CitaMapper.CitaDTO> getAllAppointments() {
        var citas = citaRepository.findAll();
        return citas.stream().map(CitaMapper::toDTO).collect(java.util.stream.Collectors.toList());
    }

    /** Elimina una cita por ID (delegado al puerto). */
    public void delete(Long id) {
        citaRepository.deleteById(id);
    }

    /** Cancela una cita: marca estado y persiste (no gestiona horarios aquí). */
    public void cancel(Long id) {
        var cita = citaRepository.findById(id).orElseThrow(() -> new RuntimeException("Cita no encontrada"));
        cita.setEstado("CANCELADA");
        citaRepository.save(cita);
    }
    
    /**
     * Busca una cita por ID.
     * 
     * @param citaId ID de la cita
     * @return DTO de la cita
     */
    public CitaMapper.CitaDTO getAppointmentById(Long citaId) {
        var cita = citaRepository.findById(citaId)
            .orElseThrow(() -> new DomainException("Cita no encontrada", "APPOINTMENT_NOT_FOUND"));
        return CitaMapper.toDTO(cita);
    }
    
    /**
     * Obtiene todas las citas de un usuario.
     * 
     * @param usuarioId ID del usuario
     * @return Lista de DTOs de citas
     */
    public java.util.List<CitaMapper.CitaDTO> getAppointmentsByUsuario(Long usuarioId) {
        var citas = citaRepository.findByUsuarioId(usuarioId);
        return citas.stream()
            .map(CitaMapper::toDTO)
            .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Obtiene todas las citas de un doctor.
     * 
     * @param doctorId ID del doctor
     * @return Lista de DTOs de citas
     */
    public java.util.List<CitaMapper.CitaDTO> getAppointmentsByDoctor(Long doctorId) {
        var citas = citaRepository.findByDoctorId(doctorId);
        return citas.stream()
            .map(CitaMapper::toDTO)
            .collect(java.util.stream.Collectors.toList());
    }
}
