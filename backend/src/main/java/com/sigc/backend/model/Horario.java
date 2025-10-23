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

    @NotNull(message = "La fecha es obligatoria")
    @FutureOrPresent(message = "La fecha debe ser hoy o posterior")
    private LocalDate fecha;

    @NotBlank(message = "El turno es obligatorio")
    @Pattern(regexp = "^(Mañana|Tarde|Noche)$", message = "El turno debe ser Mañana, Tarde o Noche")
    private String turno;

    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime horaInicio;

    @NotNull(message = "La hora de fin es obligatoria")
    private LocalTime horaFin;

    private boolean disponible = true;

    // 👇 Asegúrate de que la relación se llame igual que en el frontend
    @ManyToOne
    @JoinColumn(name = "idDoctor")
    @NotNull(message = "Debe seleccionar un doctor")
    private Doctor doctor;
}
