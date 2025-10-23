package com.sigc.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "citas")
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCita;

    private LocalDate fechaCita;
    private LocalTime horaCita;
    private String turno;

    @ManyToOne
    @JoinColumn(name = "idUsuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "idDoctor")
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "idHorario")
    private Horario horario;

    private String estado = "ACTIVA"; // ACTIVA | CANCELADA | COMPLETADA
}
