package com.example.semana07.controller;

import com.example.semana07.entity.Personaje;
import com.example.semana07.service.PersonajeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/personajes")
@CrossOrigin(origins = "*")
public class PersonajeController {

    @Autowired
    private PersonajeService personajeService;

    @Value("${file.upload-dir:./imagenes/}")
    private String uploadDir;

    private static final String CARPETA = "personajes";

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
        return personajeService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crear(
            @RequestParam("nombre") String nombre,
            @RequestParam("categoria") String categoria,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile,
            @RequestParam(value = "imagenOriginalFile", required = false) MultipartFile imagenOriginalFile) {
        try {
            Personaje nuevoPersonaje = new Personaje();
            nuevoPersonaje.setNombre(nombre);
            nuevoPersonaje.setCategoria(categoria);
            nuevoPersonaje.setDescripcion(descripcion != null ? descripcion : "");
            nuevoPersonaje.setEstado(1);

            if (imagenFile != null && !imagenFile.isEmpty()) {
                String imagenUrl = guardarImagen(imagenFile, CARPETA);
                nuevoPersonaje.setImagenUrl(imagenUrl);
            }

            if (imagenOriginalFile != null && !imagenOriginalFile.isEmpty()) {
                String imagenOriginalUrl = guardarImagen(imagenOriginalFile, CARPETA + "/original");
                nuevoPersonaje.setImagenOriginalUrl(imagenOriginalUrl);
            }

            Personaje guardado = personajeService.guardar(nuevoPersonaje);
            return ResponseEntity.ok(guardado);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Long id,
            @RequestParam("nombre") String nombre,
            @RequestParam("categoria") String categoria,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile,
            @RequestParam(value = "imagenOriginalFile", required = false) MultipartFile imagenOriginalFile) {
        try {
            Personaje personaje = personajeService.obtenerPorId(id)
                    .orElseThrow(() -> new RuntimeException("No encontrado"));

            personaje.setNombre(nombre);
            personaje.setCategoria(categoria);
            personaje.setDescripcion(descripcion != null ? descripcion : "");

            if (imagenFile != null && !imagenFile.isEmpty()) {
                if (personaje.getImagenUrl() != null) {
                    eliminarImagenAnterior(personaje.getImagenUrl());
                }
                String imagenUrl = guardarImagen(imagenFile, CARPETA);
                personaje.setImagenUrl(imagenUrl);
            }

            if (imagenOriginalFile != null && !imagenOriginalFile.isEmpty()) {
                if (personaje.getImagenOriginalUrl() != null) {
                    eliminarImagenAnterior(personaje.getImagenOriginalUrl());
                }
                String imagenOriginalUrl = guardarImagen(imagenOriginalFile, CARPETA + "/original");
                personaje.setImagenOriginalUrl(imagenOriginalUrl);
            }

            Personaje actualizado = personajeService.guardar(personaje);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id, @RequestParam Integer estado) {
        try {
            personajeService.actualizarEstado(id, estado);
            return ResponseEntity.ok(Map.of("mensaje", "Estado actualizado"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        Personaje personaje = personajeService.obtenerPorId(id).orElse(null);
        if (personaje != null) {
            if (personaje.getImagenUrl() != null) eliminarImagenAnterior(personaje.getImagenUrl());
            if (personaje.getImagenOriginalUrl() != null) eliminarImagenAnterior(personaje.getImagenOriginalUrl());
        }
        personajeService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    private String guardarImagen(MultipartFile imagen, String subCarpeta) throws IOException {
        Path rutaCarpeta = Paths.get(uploadDir, subCarpeta);
        if (!Files.exists(rutaCarpeta)) {
            Files.createDirectories(rutaCarpeta);
        }

        String extension = "";
        String nombreOriginal = imagen.getOriginalFilename();
        if (nombreOriginal != null && nombreOriginal.contains(".")) {
            extension = nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
        }
        String nombreArchivo = UUID.randomUUID().toString() + extension;
        Path rutaCompleta = Paths.get(uploadDir, subCarpeta, nombreArchivo);
        Files.write(rutaCompleta, imagen.getBytes());
        return "/imagenes/" + subCarpeta + "/" + nombreArchivo;
    }

    private void eliminarImagenAnterior(String imagenUrl) {
        try {
            String rutaRelativa = imagenUrl.replace("/imagenes/", "");
            Path rutaArchivo = Paths.get(uploadDir, rutaRelativa);
            Files.deleteIfExists(rutaArchivo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}