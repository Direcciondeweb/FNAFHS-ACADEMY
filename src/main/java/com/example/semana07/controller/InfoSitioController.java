package com.example.semana07.controller;

import com.example.semana07.entity.InfoSitio;
import com.example.semana07.service.InfoSitioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/info-sitio")
public class InfoSitioController {

    @Autowired
    private InfoSitioService infoSitioService;

    @GetMapping
    public ResponseEntity<InfoSitio> obtener() {
        return ResponseEntity.ok(infoSitioService.obtener());
    }

    @PutMapping
    public ResponseEntity<InfoSitio> actualizar(@RequestBody Map<String, String> body) {
        InfoSitio actualizado = infoSitioService.actualizar(body.get("vision"), body.get("mision"));
        return ResponseEntity.ok(actualizado);
    }
}