package com.sigc.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad Usuario
 * Representa la tabla 'usuarios' en la base de datos
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @NotBlank(message = "El nombre es obligatorio")
<<<<<<< HEAD
    @Column(nullable = false)
    private String nombre;

    @Email(message = "Debe ingresar un correo válido")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    @Column(nullable = false)
    private String password;
=======
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;

    @Email(message = "Debe ingresar un correo válido")
    @NotBlank(message = "El email es obligatorio")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Column(nullable = false, length = 255)
    private String password; // Almacena hash BCrypt
>>>>>>> origin/feature/resoluciones-y-JWT

    // ✅ Valor por defecto: PACIENTE
    @Column(nullable = false)
    private String rol = "PACIENTE";

    @Pattern(regexp = "\\d{8}", message = "El DNI debe tener 8 dígitos")
    @Column(nullable = false, length = 8)
    private String dni;

    @Pattern(regexp = "\\d{9}", message = "El teléfono debe tener 9 dígitos")
    @Column(nullable = false, length = 9)
    private String telefono;

<<<<<<< HEAD
    // ✅ Campo adicional para manejo de estado
    @Column(nullable = false)
    private boolean activo = true;
=======
    @NotBlank(message = "El rol es obligatorio")
    @Column(nullable = false, length = 20)
    private String rol; // PACIENTE, DOCTOR, ADMIN

    @Column(nullable = false)
    private boolean activo = true;

    @CreationTimestamp
    @Column(name = "fecha_registro", updatable = false)
    private LocalDateTime fechaRegistro;
>>>>>>> origin/feature/resoluciones-y-JWT
}
