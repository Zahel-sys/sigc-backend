package com.sigc.backend.config;

import org.springframework.lang.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // Mapeo general de uploads
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:C:/sigc/uploads/")
                .setCachePeriod(0);
        
        // Mapeo específico para imágenes de especialidades
        registry.addResourceHandler("/images/especialidades/**")
                .addResourceLocations("file:C:/sigc/uploads/especialidades/")
                .setCachePeriod(0);
        
        // Mapeo específico para imágenes de doctores
        registry.addResourceHandler("/images/doctores/**")
                .addResourceLocations("file:C:/sigc/uploads/doctores/")
                .setCachePeriod(0);
    }
}
