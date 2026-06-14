package com.example.semana07.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir:./imagenes/}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        String rutaAbsoluta = Paths.get(uploadDir).toAbsolutePath().toString() + "/";
        registry.addResourceHandler("/imagenes/**")
                .addResourceLocations("file:" + rutaAbsoluta);
    }
}