package com.sigc.backend.adapter.out.persistence;

import com.sigc.backend.application.mapper.HorarioMapper;
import com.sigc.backend.domain.model.Horario;
import com.sigc.backend.domain.port.IHorarioRepository;
import com.sigc.backend.repository.HorarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador JPA que implementa IHorarioRepository
 * Conecta el dominio con la capa de persistencia JPA
 */
@Component
@RequiredArgsConstructor
public class JpaHorarioAdapter implements IHorarioRepository {
    
    private final HorarioRepository jpaRepository;
    private final HorarioMapper mapper;
    
    @Override
    public List<Horario> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<Horario> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }
    
    @Override
    public List<Horario> findByIdDoctor(Long idDoctor) {
        return jpaRepository.findAll().stream()
                .filter(h -> h.getDoctor() != null && h.getDoctor().getIdDoctor() != null && h.getDoctor().getIdDoctor().equals(idDoctor))
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Horario> findByDisponibleTrue() {
        return jpaRepository.findAll().stream()
                .filter(com.sigc.backend.model.Horario::isDisponible)
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Horario> findByFecha(LocalDate fecha) {
        return jpaRepository.findAll().stream()
                .filter(h -> h.getFecha().equals(fecha))
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Horario> findByIdDoctorAndFechaAndDisponibleTrue(Long idDoctor, LocalDate fecha) {
        return jpaRepository.findAll().stream()
                .filter(h -> h.getDoctor() != null 
                        && h.getDoctor().getIdDoctor() != null
                        && h.getDoctor().getIdDoctor().equals(idDoctor)
                        && h.getFecha() != null
                        && h.getFecha().equals(fecha)
                        && h.isDisponible())
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public Horario save(Horario horario) {
        com.sigc.backend.model.Horario jpaEntity = mapper.toJpaEntity(horario);
        if (jpaEntity == null) {
            throw new IllegalArgumentException("No se pudo convertir el horario a entidad JPA");
        }
        com.sigc.backend.model.Horario saved = jpaRepository.save(jpaEntity);
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
