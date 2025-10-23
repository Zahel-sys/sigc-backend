package com.sigc.backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

@RestController
@RequestMapping("/api/uploads")
@CrossOrigin(origins = "http://localhost:5173") // tu frontend
public class UploadController {

    @Value("${upload.path:src/main/resources/uploads}")
    private String uploadPath;

    @PostMapping
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Archivo vacío");
        }

        // Crea la carpeta si no existe
        File dir = new File(uploadPath);
        if (!dir.exists()) dir.mkdirs();

        // Genera un nombre único
        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadPath, filename);

        // Guarda el archivo
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Devuelve la ruta relativa
        String fileUrl = "/uploads/" + filename;
        return ResponseEntity.ok(fileUrl);
    }
}
