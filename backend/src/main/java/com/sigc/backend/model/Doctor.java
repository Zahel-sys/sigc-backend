package com.sigc.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "doctores")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDoctor;

    private String nombre;
    private String especialidad;
    private int cupoPacientes; // límite de pacientes por turno
}
