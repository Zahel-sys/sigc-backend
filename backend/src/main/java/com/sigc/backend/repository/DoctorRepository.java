package com.sigc.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.sigc.backend.model.Doctor;

public interface DoctorRepository extends JpaRepository<Doctor, Long> { }
