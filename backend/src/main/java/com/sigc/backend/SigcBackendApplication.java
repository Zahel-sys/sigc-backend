package com.sigc.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class SigcBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SigcBackendApplication.class, args);
	}

	// 👇 Agrega este método
	@Bean
	public WebMvcConfigurer webMvcConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addResourceHandlers(ResourceHandlerRegistry registry) {
				// Sirve archivos estáticos desde la carpeta "uploads"
				registry.addResourceHandler("/uploads/**")
						.addResourceLocations("file:src/main/resources/uploads/");
			}
		};
	}
}
