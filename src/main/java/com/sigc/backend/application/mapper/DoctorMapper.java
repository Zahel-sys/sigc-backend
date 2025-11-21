package com.sigc.backend.application.mapper;

import com.sigc.backend.domain.model.Doctor;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre entidad JPA Doctor y modelo de dominio Doctor
 */
@Component
public class DoctorMapper {
    
    /**
     * Convierte entidad JPA a modelo de dominio
     */
    public Doctor toDomain(com.sigc.backend.model.Doctor jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }
        
        return Doctor.builder()
                .idDoctor(jpaEntity.getIdDoctor())
                .nombre(jpaEntity.getNombre())
                .especialidad(jpaEntity.getEspecialidad())
                .cupoPacientes(jpaEntity.getCupoPacientes())
                .imagen(jpaEntity.getImagen())
                .build();
    }
    
    /**
     * Convierte modelo de dominio a entidad JPA
     */
    public com.sigc.backend.model.Doctor toJpaEntity(Doctor domain) {
        if (domain == null) {
            return null;
        }
        
        com.sigc.backend.model.Doctor jpaEntity = new com.sigc.backend.model.Doctor();
        jpaEntity.setIdDoctor(domain.getIdDoctor());
        jpaEntity.setNombre(domain.getNombre());
        jpaEntity.setEspecialidad(domain.getEspecialidad());
        jpaEntity.setCupoPacientes(domain.getCupoPacientes());
        jpaEntity.setImagen(domain.getImagen());
        
        return jpaEntity;
    }
}
