package com.example.semana07.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController {

    @Value("${spring.datasource.url:NO_DEFINIDA}")
    private String dbUrl;

    @Value("${spring.datasource.driver-class-name:NO_DEFINIDA}")
    private String dbDriver;

    @Value("${spring.datasource.username:NO_DEFINIDA}")
    private String dbUsername;

    @Value("${cloudinary.cloud-name:NO_DEFINIDA}")
    private String cloudinaryName;

    @GetMapping("/test")
    public String test() {
        return "La aplicacion funciona correctamente!";
    }

    @GetMapping("/debug/config")
    public Map<String, String> debugConfig() {
        Map<String, String> info = new HashMap<>();

        // Enmascaramos la URL para no exponer credenciales completas si tiene user/pass embebido
        String urlMostrar = dbUrl;
        if (urlMostrar.length() > 40) {
            urlMostrar = urlMostrar.substring(0, 40) + "...(cortado)";
        }

        info.put("datasource_url_empieza_con", urlMostrar);
        info.put("datasource_driver", dbDriver);
        info.put("datasource_username", dbUsername.isEmpty() ? "VACIO" : dbUsername);
        info.put("es_h2_memoria", String.valueOf(dbUrl.contains("h2:mem")));
        info.put("cloudinary_name_definido", cloudinaryName.equals("NO_DEFINIDA") || cloudinaryName.isEmpty() ? "VACIO_O_AUSENTE" : "DEFINIDO");

        return info;
    }
}