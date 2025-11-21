package com.sigc.backend.adapter.out.persistence;

import com.sigc.backend.application.mapper.EspecialidadMapper;
import com.sigc.backend.domain.model.Especialidad;
import com.sigc.backend.domain.port.IEspecialidadRepository;
import com.sigc.backend.repository.EspecialidadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador JPA que implementa IEspecialidadRepository
 * Conecta el dominio con la capa de persistencia JPA
 */
@Component
@RequiredArgsConstructor
public class JpaEspecialidadAdapter implements IEspecialidadRepository {
    
    private final EspecialidadRepository jpaRepository;
    private final EspecialidadMapper mapper;
    
    @Override
    public List<Especialidad> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<Especialidad> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }
    
    @Override
    public Optional<Especialidad> findByNombre(String nombre) {
        return jpaRepository.findAll().stream()
                .filter(e -> e.getNombre() != null && e.getNombre().equalsIgnoreCase(nombre))
                .findFirst()
                .map(mapper::toDomain);
    }
    
    @Override
    public Especialidad save(Especialidad especialidad) {
        com.sigc.backend.model.Especialidad jpaEntity = mapper.toJpaEntity(especialidad);
        com.sigc.backend.model.Especialidad saved = jpaRepository.save(jpaEntity);
        return mapper.toDomain(saved);
    }
    
    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
    
    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }
}
