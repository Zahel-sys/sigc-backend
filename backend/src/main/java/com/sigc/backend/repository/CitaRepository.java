package com.sigc.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sigc.backend.model.Cita;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {
}
