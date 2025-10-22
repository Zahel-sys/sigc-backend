package com.sigc.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.sigc.backend.model.Especialidad;

public interface EspecialidadRepository extends JpaRepository<Especialidad, Long> { }