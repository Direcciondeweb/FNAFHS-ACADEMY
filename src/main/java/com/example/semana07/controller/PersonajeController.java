package com.example.semana07.controller;

import com.example.semana07.entity.Personaje;
import com.example.semana07.entity.Usuario;
import com.example.semana07.service.CloudStorageService;
import com.example.semana07.service.PersonajeService;
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
@RequestMapping("/api/personajes")
public class PersonajeController {

    @Autowired
    private PersonajeService personajeService;

    @Autowired
    private CloudStorageService cloudStorageService;

    @GetMapping
    public ResponseEntity<List<Personaje>> listar() {
        return ResponseEntity.ok(personajeService.listarTodos());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<Personaje>> listarActivos() {
        return ResponseEntity.ok(personajeService.listarActivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Personaje> obtener(@PathVariable Long id) {
        return personajeService.obtenerPorId(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crear(
            @RequestParam("nombre") String nombre,
            @RequestParam("categoria") String categoria,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile,
            @RequestParam(value = "imagenOriginalFile", required = false) MultipartFile imagenOriginalFile,
            HttpSession session) {
        try {
            Personaje nuevoPersonaje = new Personaje();
            nuevoPersonaje.setNombre(nombre);
            nuevoPersonaje.setCategoria(categoria);
            nuevoPersonaje.setDescripcion(descripcion != null ? descripcion : "");
            nuevoPersonaje.setEstado(1);

            if (imagenFile != null && !imagenFile.isEmpty()) {
                String url = cloudStorageService.uploadFile(imagenFile, "personajes");
                nuevoPersonaje.setImagenUrl(url);
            }
            if (imagenOriginalFile != null && !imagenOriginalFile.isEmpty()) {
                String url = cloudStorageService.uploadFile(imagenOriginalFile, "personajes/original");
                nuevoPersonaje.setImagenOriginalUrl(url);
            }

            Map<String, String> sesion = datosSesion(session);
            Personaje guardado = personajeService.guardar(nuevoPersonaje, sesion.get("usuario"), sesion.get("rol"));
            return ResponseEntity.ok(guardado);

        } catch (Exception e) {
            // Log completo en la consola de Render para ver la causa real del fallo
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Error al crear personaje",
                            "detalle", e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName()
                    ));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Long id,
            @RequestParam("nombre") String nombre,
            @RequestParam("categoria") String categoria,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile,
            @RequestParam(value = "imagenOriginalFile", required = false) MultipartFile imagenOriginalFile,
            HttpSession session) {
        try {
            Personaje personaje = personajeService.obtenerPorId(id)
                    .orElseThrow(() -> new RuntimeException("No encontrado"));

            personaje.setNombre(nombre);
            personaje.setCategoria(categoria);
            personaje.setDescripcion(descripcion != null ? descripcion : "");

            if (imagenFile != null && !imagenFile.isEmpty()) {
                if (personaje.getImagenUrl() != null) {
                    String publicId = cloudStorageService.extraerPublicId(personaje.getImagenUrl());
                    cloudStorageService.deleteFile(publicId);
                }
                String url = cloudStorageService.uploadFile(imagenFile, "personajes");
                personaje.setImagenUrl(url);
            }
            if (imagenOriginalFile != null && !imagenOriginalFile.isEmpty()) {
                if (personaje.getImagenOriginalUrl() != null) {
                    String publicId = cloudStorageService.extraerPublicId(personaje.getImagenOriginalUrl());
                    cloudStorageService.deleteFile(publicId);
                }
                String url = cloudStorageService.uploadFile(imagenOriginalFile, "personajes/original");
                personaje.setImagenOriginalUrl(url);
            }

            Map<String, String> sesion = datosSesion(session);
            Personaje actualizado = personajeService.guardar(personaje, sesion.get("usuario"), sesion.get("rol"));
            return ResponseEntity.ok(actualizado);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Error al actualizar personaje",
                            "detalle", e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName()
                    ));
        }
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id, @RequestParam Integer estado) {
        try {
            personajeService.actualizarEstado(id, estado);
            return ResponseEntity.ok(Map.of("mensaje", "Estado actualizado"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id, HttpSession session) {
        try {
            Personaje personaje = personajeService.obtenerPorId(id).orElse(null);

            if (personaje != null) {
                if (personaje.getImagenUrl() != null) {
                    cloudStorageService.deleteFile(cloudStorageService.extraerPublicId(personaje.getImagenUrl()));
                }
                if (personaje.getImagenOriginalUrl() != null) {
                    cloudStorageService.deleteFile(cloudStorageService.extraerPublicId(personaje.getImagenOriginalUrl()));
                }
            }

            Map<String, String> sesion = datosSesion(session);
            personajeService.eliminar(id, sesion.get("usuario"), sesion.get("rol"));
            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Error al eliminar personaje",
                            "detalle", e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName()
                    ));
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