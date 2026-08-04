package com.example.semana07.controller;

import com.example.semana07.entity.SliderImage;
import com.example.semana07.entity.Usuario;
import com.example.semana07.service.CloudStorageService;
import com.example.semana07.service.SliderImageService;
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

@RestController
@RequestMapping("/api/slider")
public class SliderController {

    @Autowired
    private SliderImageService sliderImageService;

    @Autowired
    private CloudStorageService cloudStorageService;

    @GetMapping
    public ResponseEntity<List<SliderImage>> listar() {
        return ResponseEntity.ok(sliderImageService.listarTodas());
    }

    @GetMapping("/activas")
    public ResponseEntity<List<SliderImage>> listarActivas() {
        return ResponseEntity.ok(sliderImageService.listarActivas());
    }

    @PostMapping
    public ResponseEntity<?> subirImagen(@RequestParam("imagen") MultipartFile imagen, HttpSession session) {
        try {
            if (imagen.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "No se seleccionó ningún archivo"));
            }

            String url = cloudStorageService.uploadFile(imagen, "slider");

            SliderImage nuevaImagen = new SliderImage();
            nuevaImagen.setImagenUrl(url);
            nuevaImagen.setActivo(false);
            nuevaImagen.setOrden(sliderImageService.listarTodas().size());

            Map<String, String> sesion = datosSesion(session);
            SliderImage guardada = sliderImageService.guardar(nuevaImagen, sesion.get("usuario"), sesion.get("rol"));
            return ResponseEntity.ok(guardada);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/toggle")
    public ResponseEntity<?> toggleImagen(@PathVariable Long id, @RequestParam Boolean activo, HttpSession session) {
        try {
            Map<String, String> sesion = datosSesion(session);
            sliderImageService.toggleActivo(id, activo, sesion.get("usuario"), sesion.get("rol"));
            return ResponseEntity.ok(Map.of("mensaje", "Estado actualizado"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id, HttpSession session) {
        try {
            List<SliderImage> todas = sliderImageService.listarTodas();
            for (SliderImage img : todas) {
                if (img.getId().equals(id) && img.getImagenUrl() != null) {
                    String publicId = cloudStorageService.extraerPublicId(img.getImagenUrl());
                    cloudStorageService.deleteFile(publicId);
                    break;
                }
            }
            Map<String, String> sesion = datosSesion(session);
            sliderImageService.eliminar(id, sesion.get("usuario"), sesion.get("rol"));
            return ResponseEntity.ok(Map.of("mensaje", "Imagen eliminada"));
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