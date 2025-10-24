package com.sigc.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para respuesta de registro exitoso
 * No incluye datos sensibles como password
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroResponse {
    
    private Long idUsuario;
    private String nombre;
    private String email;
    private String mensaje;
}
