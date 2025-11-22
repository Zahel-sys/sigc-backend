package com.sigc.backend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {

        @Value("${app.upload.dir:uploads/}")
        private String appUploadDir; // base directory for uploads (configurable)

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        log.info("🔧 Configurando Resource Handlers para servir archivos estáticos");
        
        // Normalizar appUploadDir
        String base = appUploadDir == null ? "uploads/" : appUploadDir;
        if (!base.endsWith("/")) base = base + "/";

        // Mapeo de uploads/especialidades/
        registry.addResourceHandler("/uploads/especialidades/**")
                .addResourceLocations("file:" + base + "especialidades/")
                .setCachePeriod(0);
        log.info("✅ Handler configurado: /uploads/especialidades/** -> file:{}", base + "especialidades/");

        // Mapeo de uploads/doctores/
        registry.addResourceHandler("/uploads/doctores/**")
                .addResourceLocations("file:" + base + "doctores/")
                .setCachePeriod(0);
        log.info("✅ Handler configurado: /uploads/doctores/** -> file:{}", base + "doctores/");

        // Mapeo general de uploads (fallback)
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + base)
                .setCachePeriod(0);
        log.info("✅ Handler configurado: /uploads/** -> file:{}", base);
    }
}
