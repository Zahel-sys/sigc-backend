package com.sigc.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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

    @FutureOrPresent(message = "La fecha debe ser hoy o posterior")
    private LocalDate fecha;

    @NotBlank(message = "El turno es obligatorio")
    @Pattern(regexp = "^(Mañana|Tarde|Noche)$", message = "El turno debe ser Mañana, Tarde o Noche")
    private String turno;

    private LocalTime horaInicio;
    private LocalTime horaFin;

    private boolean disponible = true;

    @ManyToOne
    @JoinColumn(name = "id_doctor")
    private Doctor doctor;
}
