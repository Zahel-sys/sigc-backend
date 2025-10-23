package com.sigc.backend.repository;

import com.sigc.backend.model.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {
    List<Cita> findByUsuario_IdUsuario(Long idUsuario);
}
