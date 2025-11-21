package com.sigc.backend.application.mapper;

import com.sigc.backend.domain.model.Especialidad;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre entidad JPA Especialidad y modelo de dominio Especialidad
 */
@Component
public class EspecialidadMapper {
    
    /**
     * Convierte entidad JPA a modelo de dominio
     */
    public Especialidad toDomain(com.sigc.backend.model.Especialidad jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }
        
        return Especialidad.builder()
                .idEspecialidad(jpaEntity.getIdEspecialidad())
                .nombre(jpaEntity.getNombre())
                .descripcion(jpaEntity.getDescripcion())
                .imagen(jpaEntity.getImagen())
                .build();
    }
    
    /**
     * Convierte modelo de dominio a entidad JPA
     */
    public com.sigc.backend.model.Especialidad toJpaEntity(Especialidad domain) {
        if (domain == null) {
            return null;
        }
        
        com.sigc.backend.model.Especialidad jpaEntity = new com.sigc.backend.model.Especialidad();
        jpaEntity.setIdEspecialidad(domain.getIdEspecialidad());
        jpaEntity.setNombre(domain.getNombre());
        jpaEntity.setDescripcion(domain.getDescripcion());
        jpaEntity.setImagen(domain.getImagen());
        
        return jpaEntity;
    }
}
