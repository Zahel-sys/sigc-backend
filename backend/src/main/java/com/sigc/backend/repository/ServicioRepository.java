package com.sigc.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sigc.backend.model.Servicio;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Long> {
}
