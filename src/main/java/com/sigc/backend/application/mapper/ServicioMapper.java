package com.sigc.backend.application.mapper;

import com.sigc.backend.domain.model.Servicio;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre entidad JPA Servicio y modelo de dominio Servicio
 */
@Component
public class ServicioMapper {
    
    /**
     * Convierte entidad JPA a modelo de dominio
     */
    public Servicio toDomain(com.sigc.backend.model.Servicio jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }
        
        return Servicio.builder()
                .idServicio(jpaEntity.getIdServicio())
                .nombreServicio(jpaEntity.getNombreServicio())
                .descripcion(jpaEntity.getDescripcion())
                .duracionMinutos(jpaEntity.getDuracionMinutos())
                .precio(jpaEntity.getPrecio())
                .build();
    }
    
    /**
     * Convierte modelo de dominio a entidad JPA
     */
    public com.sigc.backend.model.Servicio toJpaEntity(Servicio domain) {
        if (domain == null) {
            return null;
        }
        
        com.sigc.backend.model.Servicio jpaEntity = new com.sigc.backend.model.Servicio();
        jpaEntity.setIdServicio(domain.getIdServicio());
        jpaEntity.setNombreServicio(domain.getNombreServicio());
        jpaEntity.setDescripcion(domain.getDescripcion());
        jpaEntity.setDuracionMinutos(domain.getDuracionMinutos());
        jpaEntity.setPrecio(domain.getPrecio());
        
        return jpaEntity;
    }
}
