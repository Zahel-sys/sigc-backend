package com.sigc.backend.repository;

import com.sigc.backend.model.Horario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HorarioRepository extends JpaRepository<Horario, Long> {
    List<Horario> findByDoctor_IdDoctorAndDisponibleTrue(Long idDoctor);
}
