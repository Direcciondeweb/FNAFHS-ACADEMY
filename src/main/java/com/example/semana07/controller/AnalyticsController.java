package com.example.semana07.controller;

import com.example.semana07.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/dashboard")
    public ResponseEntity<?> dashboard() {
        return ResponseEntity.ok(analyticsService.dashboardStats());
    }

    @GetMapping("/top-contenido")
    public ResponseEntity<?> topContenido(@RequestParam(defaultValue = "10") int limite) {
        return ResponseEntity.ok(analyticsService.topContenidoPorLikes(limite));
    }
}