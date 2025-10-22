package com.sigc.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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

    @NotBlank(message = "El nombre del doctor es obligatorio")
    private String nombre;

    @NotBlank(message = "La especialidad es obligatoria")
    private String especialidad;

    @Min(value = 1, message = "El cupo mínimo es 1")
    @Max(value = 20, message = "El cupo máximo es 20 pacientes")
    private int cupoPacientes;
}
