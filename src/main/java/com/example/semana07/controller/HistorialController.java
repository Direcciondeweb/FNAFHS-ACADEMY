package com.example.semana07.controller;

import com.example.semana07.entity.HistorialAccion;
import com.example.semana07.service.HistorialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/historial")
public class HistorialController {

    @Autowired
    private HistorialService historialService;

    @GetMapping
    public ResponseEntity<Page<HistorialAccion>> listar(Pageable pageable) {
        return ResponseEntity.ok(historialService.listarPaginado(pageable));
    }

    @GetMapping("/usuario/{username}")
    public ResponseEntity<List<HistorialAccion>> porUsuario(@PathVariable String username) {
        return ResponseEntity.ok(historialService.listarPorUsuario(username));
    }

    @GetMapping("/entidad/{entidad}")
    public ResponseEntity<List<HistorialAccion>> porEntidad(@PathVariable String entidad) {
        return ResponseEntity.ok(historialService.listarPorEntidad(entidad));
    }

    @GetMapping("/rango")
    public ResponseEntity<List<HistorialAccion>> porRango(
            @RequestParam String desde, @RequestParam String hasta) {
        return ResponseEntity.ok(historialService.listarPorRango(
                LocalDateTime.parse(desde), LocalDateTime.parse(hasta)));
    }
}