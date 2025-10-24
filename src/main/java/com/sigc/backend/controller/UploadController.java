package com.sigc.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador para subida de imágenes
 * Endpoint: POST /uploads
 * Recibe: multipart/form-data con campo "file"
 * Retorna: URL relativa de la imagen guardada
 */
@RestController
@RequestMapping("/uploads")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
public class UploadController {

    // Directorio donde se guardarán las imágenes
    private static final String UPLOAD_DIR = "src/main/resources/static/images/especialidades/";
    
    // Extensiones de imagen permitidas
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");
    
    // Tamaño máximo de archivo: 5MB
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    /**
     * Endpoint para subir una imagen
     * POST /uploads
     * 
     * @param file Archivo multipart (campo "file" en el formulario)
     * @return URL relativa de la imagen guardada
     */
    @PostMapping
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // 1️⃣ Validar que el archivo no esté vacío
            if (file.isEmpty()) {
                return ResponseEntity
                    .badRequest()
                    .body(createErrorResponse("El archivo está vacío"));
            }

            // 2️⃣ Validar el tamaño del archivo
            if (file.getSize() > MAX_FILE_SIZE) {
                return ResponseEntity
                    .status(HttpStatus.PAYLOAD_TOO_LARGE)
                    .body(createErrorResponse("El archivo es demasiado grande (máximo 5MB)"));
            }

            // 3️⃣ Validar la extensión del archivo
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || !isValidImageExtension(originalFilename)) {
                return ResponseEntity
                    .badRequest()
                    .body(createErrorResponse("Formato de imagen no válido. Permitidos: " + ALLOWED_EXTENSIONS));
            }

            // 4️⃣ Crear el directorio si no existe
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 5️⃣ Generar nombre único para evitar colisiones
            String uniqueFileName = System.currentTimeMillis() + "_" + sanitizeFilename(originalFilename);
            String filePath = UPLOAD_DIR + uniqueFileName;

            // 6️⃣ Guardar el archivo en el sistema de archivos
            File destination = new File(filePath);
            file.transferTo(destination);

            // 7️⃣ Construir la URL relativa para acceder a la imagen
            String imageUrl = "/images/especialidades/" + uniqueFileName;

            // 8️⃣ Retornar respuesta exitosa con la URL
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("url", imageUrl);
            response.put("filename", uniqueFileName);
            response.put("size", file.getSize());
            response.put("contentType", file.getContentType());

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Error al guardar la imagen: " + e.getMessage()));
        }
    }

    /**
     * Valida si la extensión del archivo es una imagen permitida
     */
    private boolean isValidImageExtension(String filename) {
        String extension = getFileExtension(filename);
        return ALLOWED_EXTENSIONS.contains(extension.toLowerCase());
    }

    /**
     * Obtiene la extensión del archivo
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }

    /**
     * Limpia el nombre del archivo para evitar problemas de seguridad
     */
    private String sanitizeFilename(String filename) {
        // Eliminar caracteres especiales y espacios
        return filename.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    /**
     * Crea un objeto de respuesta de error
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", message);
        return error;
    }
}
