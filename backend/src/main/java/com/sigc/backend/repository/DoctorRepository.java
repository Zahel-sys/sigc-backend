package com.sigc.backend.repository;

import com.sigc.backend.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    List<Doctor> findByEspecialidad(String especialidad);
}
