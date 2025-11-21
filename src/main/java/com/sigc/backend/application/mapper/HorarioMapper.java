package com.sigc.backend.application.mapper;

import com.sigc.backend.domain.model.Horario;
import com.sigc.backend.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre entidad JPA Horario y modelo de dominio Horario
 */
@Component
@RequiredArgsConstructor
public class HorarioMapper {
    
    private final DoctorRepository doctorRepository;
    
    /**
     * Convierte entidad JPA a modelo de dominio
     */
    public Horario toDomain(com.sigc.backend.model.Horario jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }
        
        return Horario.builder()
                .idHorario(jpaEntity.getIdHorario())
                .fecha(jpaEntity.getFecha())
                .turno(jpaEntity.getTurno())
                .horaInicio(jpaEntity.getHoraInicio())
                .horaFin(jpaEntity.getHoraFin())
                .disponible(jpaEntity.isDisponible())
                .idDoctor(jpaEntity.getDoctor() != null ? jpaEntity.getDoctor().getIdDoctor() : null)
                .build();
    }
    
    /**
     * Convierte modelo de dominio a entidad JPA
     */
    public com.sigc.backend.model.Horario toJpaEntity(Horario domain) {
        if (domain == null) {
            return null;
        }
        
        com.sigc.backend.model.Horario jpaEntity = new com.sigc.backend.model.Horario();
        if (domain.getIdHorario() != null) {
            jpaEntity.setIdHorario(domain.getIdHorario());
        }
        jpaEntity.setFecha(domain.getFecha());
        jpaEntity.setTurno(domain.getTurno());
        jpaEntity.setHoraInicio(domain.getHoraInicio());
        jpaEntity.setHoraFin(domain.getHoraFin());
        jpaEntity.setDisponible(domain.isDisponible());
        
        // Cargar Doctor completo desde la base de datos para evitar lazy loading issues
        if (domain.getIdDoctor() != null) {
            com.sigc.backend.model.Doctor doctor = doctorRepository.findById(domain.getIdDoctor())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Doctor con ID " + domain.getIdDoctor() + " no encontrado"));
            jpaEntity.setDoctor(doctor);
        }
        
        return jpaEntity;
    }
}
