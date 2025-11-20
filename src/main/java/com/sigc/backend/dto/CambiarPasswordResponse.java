package com.sigc.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO para respuesta de cambio de contraseña exitoso
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CambiarPasswordResponse {

    private Long idUsuario;
    private String email;
    private String mensaje;
    private LocalDateTime timestamp;
    private boolean exitoso;

    public static CambiarPasswordResponse exitoso(Long idUsuario, String email) {
        CambiarPasswordResponse response = new CambiarPasswordResponse();
        response.setIdUsuario(idUsuario);
        response.setEmail(email);
        response.setMensaje("Contraseña actualizada correctamente");
        response.setTimestamp(LocalDateTime.now());
        response.setExitoso(true);
        return response;
    }
}
