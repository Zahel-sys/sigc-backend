package com.sigc.backend.adapter.out.persistence;

import com.sigc.backend.application.mapper.ServicioMapper;
import com.sigc.backend.domain.model.Servicio;
import com.sigc.backend.domain.port.IServicioRepository;
import com.sigc.backend.repository.ServicioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador JPA que implementa IServicioRepository
 * Conecta el dominio con la capa de persistencia JPA
 */
@Component
@RequiredArgsConstructor
public class JpaServicioAdapter implements IServicioRepository {
    
    private final ServicioRepository jpaRepository;
    private final ServicioMapper mapper;
    
    @Override
    public List<Servicio> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<Servicio> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }
    
    @Override
    public List<Servicio> findByNombreServicioContaining(String nombre) {
        return jpaRepository.findAll().stream()
                .filter(s -> s.getNombreServicio() != null && s.getNombreServicio().toLowerCase().contains(nombre.toLowerCase()))
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public Servicio save(Servicio servicio) {
        com.sigc.backend.model.Servicio jpaEntity = mapper.toJpaEntity(servicio);
        if (jpaEntity == null) {
            throw new IllegalArgumentException("No se pudo convertir el servicio a entidad JPA");
        }
        com.sigc.backend.model.Servicio saved = jpaRepository.save(jpaEntity);
        return mapper.toDomain(saved);
    }
    
    @Override
    public void deleteById(Long id) {
        if (id != null) {
            jpaRepository.deleteById(id);
        }
    }
    
    @Override
    public boolean existsById(Long id) {
        return id != null && jpaRepository.existsById(id);
    }
}
