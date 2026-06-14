package com.example.semana07.controller;

import com.example.semana07.entity.SliderImage;
import com.example.semana07.service.SliderImageService;
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
@RequestMapping("/api/slider")
@CrossOrigin(origins = "*")
public class SliderController {

    @Autowired
    private SliderImageService sliderImageService;

    @Value("${file.upload-dir:./imagenes/}")
    private String uploadDir;

    private static final String CARPETA = "slider";

    @GetMapping
    public ResponseEntity<List<SliderImage>> listar() {
        return ResponseEntity.ok(sliderImageService.listarTodas());
    }

    @GetMapping("/activas")
    public ResponseEntity<List<SliderImage>> listarActivas() {
        return ResponseEntity.ok(sliderImageService.listarActivas());
    }

    @PostMapping
    public ResponseEntity<?> subirImagen(@RequestParam("imagen") MultipartFile imagen) {
        try {
            if (imagen.isEmpty()) {
                return ResponseEntity.badRequest().body("No se seleccionó ningún archivo");
            }

            Path rutaCarpeta = Paths.get(uploadDir, CARPETA);
            if (!Files.exists(rutaCarpeta)) {
                Files.createDirectories(rutaCarpeta);
            }

            String extension = "";
            String nombreOriginal = imagen.getOriginalFilename();
            if (nombreOriginal != null && nombreOriginal.contains(".")) {
                extension = nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
            }
            String nombreArchivo = UUID.randomUUID().toString() + extension;
            Path rutaCompleta = Paths.get(uploadDir, CARPETA, nombreArchivo);
            Files.write(rutaCompleta, imagen.getBytes());

            String url = "/imagenes/" + CARPETA + "/" + nombreArchivo;

            SliderImage nuevaImagen = new SliderImage();
            nuevaImagen.setImagenUrl(url);
            nuevaImagen.setActivo(false);
            nuevaImagen.setOrden(sliderImageService.listarTodas().size());

            SliderImage guardada = sliderImageService.guardar(nuevaImagen);
            return ResponseEntity.ok(guardada);
        } catch (IOException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/{id}/toggle")
    public ResponseEntity<?> toggleImagen(@PathVariable Long id, @RequestParam Boolean activo) {
        try {
            sliderImageService.toggleActivo(id, activo);
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Estado actualizado");
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            List<SliderImage> todas = sliderImageService.listarTodas();
            for (SliderImage img : todas) {
                if (img.getId().equals(id) && img.getImagenUrl() != null) {
                    String rutaRelativa = img.getImagenUrl().replace("/imagenes/", "");
                    Path rutaArchivo = Paths.get(uploadDir, rutaRelativa);
                    Files.deleteIfExists(rutaArchivo);
                    break;
                }
            }
            sliderImageService.eliminar(id);
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Imagen eliminada");
            return ResponseEntity.ok(respuesta);
        } catch (IOException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}