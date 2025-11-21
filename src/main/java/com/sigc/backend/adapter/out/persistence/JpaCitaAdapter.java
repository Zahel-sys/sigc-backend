package com.sigc.backend.adapter.out.persistence;

import com.sigc.backend.domain.model.Cita;
import com.sigc.backend.domain.port.ICitaRepository;
import com.sigc.backend.repository.CitaRepository;
import com.sigc.backend.repository.UsuarioRepository;
import com.sigc.backend.repository.DoctorRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class JpaCitaAdapter implements ICitaRepository {

    private final CitaRepository citaRepository;
    private final UsuarioRepository usuarioRepository;
    private final DoctorRepository doctorRepository;

    public JpaCitaAdapter(CitaRepository citaRepository,
                          UsuarioRepository usuarioRepository,
                          DoctorRepository doctorRepository) {
        this.citaRepository = citaRepository;
        this.usuarioRepository = usuarioRepository;
        this.doctorRepository = doctorRepository;
    }

    @Override
    public Optional<Cita> findById(Long id) {
        return citaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Cita> findByUsuarioId(Long usuarioId) {
        if (usuarioId == null) {
            return List.of();
        }
        return citaRepository.findByUsuario_IdUsuario(usuarioId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Cita> findByDoctorId(Long doctorId) {
        if (doctorId == null) {
            return List.of();
        }
        return citaRepository.findAll().stream()
                .filter(e -> e.getDoctor() != null && e.getDoctor().getIdDoctor() != null && e.getDoctor().getIdDoctor().equals(doctorId))
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Cita save(Cita cita) {
        com.sigc.backend.model.Cita entity = toEntity(cita);
        com.sigc.backend.model.Cita saved = citaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public void deleteById(Long id) {
        citaRepository.deleteById(id);
    }

    @Override
    public long count() {
        return citaRepository.count();
    }

    @Override
    public List<Cita> findAll() {
        return citaRepository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    // --- Mapping helpers ---
    private Cita toDomain(com.sigc.backend.model.Cita e) {
        if (e == null) return null;
        Cita d = new Cita();
        d.setId(e.getIdCita());
        if (e.getUsuario() != null && e.getUsuario().getIdUsuario() != null) {
            d.setUsuarioId(e.getUsuario().getIdUsuario());
        }
        if (e.getDoctor() != null && e.getDoctor().getIdDoctor() != null) {
            d.setDoctorId(e.getDoctor().getIdDoctor());
        }
        if (e.getFechaCita() != null) {
            if (e.getHoraCita() != null) {
                d.setFecha(LocalDateTime.of(e.getFechaCita(), e.getHoraCita()));
            } else {
                d.setFecha(e.getFechaCita().atStartOfDay());
            }
        }
        d.setDescripcion(null); // not available in entity
        d.setEstado(e.getEstado());
        return d;
    }

    private com.sigc.backend.model.Cita toEntity(Cita d) {
        com.sigc.backend.model.Cita e = new com.sigc.backend.model.Cita();
        e.setIdCita(d.getId());
        if (d.getFecha() != null) {
            e.setFechaCita(d.getFecha().toLocalDate());
            e.setHoraCita(d.getFecha().toLocalTime());
        }
        e.setEstado(d.getEstado());
        if (d.getUsuarioId() != null) {
            e.setUsuario(usuarioRepository.findById(d.getUsuarioId()).orElse(null));
        }
        if (d.getDoctorId() != null) {
            e.setDoctor(doctorRepository.findById(d.getDoctorId()).orElse(null));
        }
        // horario association not set here
        return e;
    }
}
