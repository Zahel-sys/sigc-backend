package com.sigc.backend.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

@Slf4j
@Component
public class UploadDirectoryInitializer {

    @Value("${app.upload.dir:uploads/}")
    private String appUploadDir;

    @PostConstruct
    public void ensureUploadDirs() {
        String base = appUploadDir == null ? "uploads/" : appUploadDir;
        if (!base.endsWith("/")) base = base + "/";

        File baseDir = new File(base);
        File doctores = new File(base + "doctores/");
        File especialidades = new File(base + "especialidades/");

        if (!baseDir.exists() && baseDir.mkdirs()) {
            log.info("✅ Directorio base de uploads creado: {}", baseDir.getAbsolutePath());
        }
        if (!doctores.exists() && doctores.mkdirs()) {
            log.info("✅ Directorio de doctores creado: {}", doctores.getAbsolutePath());
        }
        if (!especialidades.exists() && especialidades.mkdirs()) {
            log.info("✅ Directorio de especialidades creado: {}", especialidades.getAbsolutePath());
        }
        log.info("? UploadDirectoryInitializer finalizado. Base: {}", base);
    }
}
