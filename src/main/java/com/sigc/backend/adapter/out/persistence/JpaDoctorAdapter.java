package com.sigc.backend.adapter.out.persistence;

import com.sigc.backend.application.mapper.DoctorMapper;
import com.sigc.backend.domain.model.Doctor;
import com.sigc.backend.domain.port.IDoctorRepository;
import com.sigc.backend.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador JPA que implementa IDoctorRepository
 * Conecta el dominio con la capa de persistencia JPA
 */
@Component
@RequiredArgsConstructor
public class JpaDoctorAdapter implements IDoctorRepository {
    
    private final DoctorRepository jpaRepository;
    private final DoctorMapper mapper;
    
    @Override
    public List<Doctor> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<Doctor> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }
    
    @Override
    public List<Doctor> findByEspecialidad(String especialidad) {
        return jpaRepository.findAll().stream()
                .filter(d -> d.getEspecialidad() != null && d.getEspecialidad().equalsIgnoreCase(especialidad))
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public Doctor save(Doctor doctor) {
        com.sigc.backend.model.Doctor jpaEntity = mapper.toJpaEntity(doctor);
        if (jpaEntity == null) {
            throw new IllegalArgumentException("No se pudo convertir el doctor a entidad JPA");
        }
        com.sigc.backend.model.Doctor saved = jpaRepository.save(jpaEntity);
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
