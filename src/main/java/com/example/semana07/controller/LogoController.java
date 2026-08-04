package com.example.semana07.controller;

import com.example.semana07.entity.Logo;
import com.example.semana07.entity.Usuario;
import com.example.semana07.service.CloudStorageService;
import com.example.semana07.service.LogoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/logo")
public class LogoController {

    @Autowired
    private LogoService logoService;

    @Autowired
    private CloudStorageService cloudStorageService;

    @GetMapping
    public ResponseEntity<List<Logo>> listar() {
        return ResponseEntity.ok(logoService.listarTodos());
    }

    @GetMapping("/activo")
    public ResponseEntity<?> obtenerActivo() {
        Logo activo = logoService.obtenerActivo();
        if (activo != null) return ResponseEntity.ok(activo);
        Map<String, Object> response = new HashMap<>();
        response.put("activo", false);
        response.put("imagenUrl", null);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<?> subir(@RequestParam("imagen") MultipartFile imagen, HttpSession session) {
        try {
            if (imagen.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "No se seleccionó ningún archivo"));
            }

            String url = cloudStorageService.uploadFile(imagen, "logo");

            Logo nuevoLogo = new Logo();
            nuevoLogo.setImagenUrl(url);
            nuevoLogo.setTitulo("Logo " + System.currentTimeMillis());
            nuevoLogo.setActivo(false);

            Map<String, String> sesion = datosSesion(session);
            Logo guardado = logoService.guardar(nuevoLogo, sesion.get("usuario"), sesion.get("rol"));
            return ResponseEntity.ok(guardado);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/activar")
    public ResponseEntity<?> activar(@PathVariable Long id, HttpSession session) {
        try {
            Map<String, String> sesion = datosSesion(session);
            logoService.activar(id, sesion.get("usuario"), sesion.get("rol"));
            return ResponseEntity.ok(Map.of("mensaje", "Logo activado correctamente"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id, HttpSession session) {
        try {
            Optional<Logo> logoOpt = logoService.obtenerPorId(id);
            if (logoOpt.isPresent() && logoOpt.get().getImagenUrl() != null) {
                String publicId = cloudStorageService.extraerPublicId(logoOpt.get().getImagenUrl());
                cloudStorageService.deleteFile(publicId);
            }
            Map<String, String> sesion = datosSesion(session);
            logoService.eliminar(id, sesion.get("usuario"), sesion.get("rol"));
            return ResponseEntity.ok(Map.of("mensaje", "Logo eliminado correctamente"));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    private Map<String, String> datosSesion(HttpSession session) {
        Map<String, String> datos = new HashMap<>();
        Object usuarioObj = session.getAttribute("usuario");
        if (usuarioObj instanceof Usuario usuario) {
            datos.put("usuario", usuario.getUsername());
            datos.put("rol", usuario.getRol());
        } else {
            datos.put("usuario", "desconocido");
            datos.put("rol", "N/A");
        }
        return datos;
    }
}