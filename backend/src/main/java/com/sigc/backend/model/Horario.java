package com.sigc.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "horarios")
public class Horario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idHorario;

    private LocalDate fecha;
    private String turno; // Mañana, Tarde, Noche
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private boolean disponible = true;

    @ManyToOne
    @JoinColumn(name = "id_doctor")
    private Doctor doctor;
}
