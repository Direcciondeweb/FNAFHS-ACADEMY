package com.example.semana07.controller;

import com.example.semana07.entity.Personaje;
import com.example.semana07.entity.Usuario;
import com.example.semana07.service.PersonajeService;
import jakarta.servlet.http.HttpSession;
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
import java.util.HashMap;
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
                nuevoPersonaje.setImagenUrl(guardarImagen(imagenFile, CARPETA));
            }
            if (imagenOriginalFile != null && !imagenOriginalFile.isEmpty()) {
                nuevoPersonaje.setImagenOriginalUrl(guardarImagen(imagenOriginalFile, CARPETA + "/original"));
            }

            Map<String, String> sesion = datosSesion(session);
            Personaje guardado = personajeService.guardar(nuevoPersonaje, sesion.get("usuario"), sesion.get("rol"));
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
            @RequestParam(value = "imagenOriginalFile", required = false) MultipartFile imagenOriginalFile,
            HttpSession session) {
        try {
            Personaje personaje = personajeService.obtenerPorId(id)
                    .orElseThrow(() -> new RuntimeException("No encontrado"));

            personaje.setNombre(nombre);
            personaje.setCategoria(categoria);
            personaje.setDescripcion(descripcion != null ? descripcion : "");

            if (imagenFile != null && !imagenFile.isEmpty()) {
                if (personaje.getImagenUrl() != null) eliminarImagenAnterior(personaje.getImagenUrl());
                personaje.setImagenUrl(guardarImagen(imagenFile, CARPETA));
            }
            if (imagenOriginalFile != null && !imagenOriginalFile.isEmpty()) {
                if (personaje.getImagenOriginalUrl() != null) eliminarImagenAnterior(personaje.getImagenOriginalUrl());
                personaje.setImagenOriginalUrl(guardarImagen(imagenOriginalFile, CARPETA + "/original"));
            }

            Map<String, String> sesion = datosSesion(session);
            Personaje actualizado = personajeService.guardar(personaje, sesion.get("usuario"), sesion.get("rol"));
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
    public ResponseEntity<Void> eliminar(@PathVariable Long id, HttpSession session) {
        Personaje personaje = personajeService.obtenerPorId(id).orElse(null);
        if (personaje != null) {
            if (personaje.getImagenUrl() != null) eliminarImagenAnterior(personaje.getImagenUrl());
            if (personaje.getImagenOriginalUrl() != null) eliminarImagenAnterior(personaje.getImagenOriginalUrl());
        }
        Map<String, String> sesion = datosSesion(session);
        personajeService.eliminar(id, sesion.get("usuario"), sesion.get("rol"));
        return ResponseEntity.noContent().build();
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

    private String guardarImagen(MultipartFile imagen, String subCarpeta) throws IOException {
        Path rutaCarpeta = Paths.get(uploadDir, subCarpeta);
        if (!Files.exists(rutaCarpeta)) Files.createDirectories(rutaCarpeta);
        String extension = "";
        String nombreOriginal = imagen.getOriginalFilename();
        if (nombreOriginal != null && nombreOriginal.contains(".")) {
            extension = nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
        }
        String nombreArchivo = UUID.randomUUID().toString() + extension;
        Files.write(Paths.get(uploadDir, subCarpeta, nombreArchivo), imagen.getBytes());
        return "/imagenes/" + subCarpeta + "/" + nombreArchivo;
    }

    private void eliminarImagenAnterior(String imagenUrl) {
        try {
            String rutaRelativa = imagenUrl.replace("/imagenes/", "");
            Files.deleteIfExists(Paths.get(uploadDir, rutaRelativa));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}