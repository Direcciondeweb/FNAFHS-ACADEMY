package com.example.semana07.controller;

import com.example.semana07.entity.Logo;
import com.example.semana07.service.LogoService;
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
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/logo")
@CrossOrigin(origins = "*")
public class LogoController {

    @Autowired
    private LogoService logoService;

    @Value("${file.upload-dir:./imagenes/}")
    private String uploadDir;

    private static final String CARPETA = "logo";

    @GetMapping
    public ResponseEntity<List<Logo>> listar() {
        return ResponseEntity.ok(logoService.listarTodos());
    }

    @GetMapping("/activo")
    public ResponseEntity<?> obtenerActivo() {
        Logo activo = logoService.obtenerActivo();
        if (activo != null) {
            return ResponseEntity.ok(activo);
        }
        Map<String, Object> response = new HashMap<>();
        response.put("activo", false);
        response.put("imagenUrl", null);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<?> subir(@RequestParam("imagen") MultipartFile imagen) {
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

            String imagenUrl = "/imagenes/" + CARPETA + "/" + nombreArchivo;

            Logo nuevoLogo = new Logo();
            nuevoLogo.setImagenUrl(imagenUrl);
            nuevoLogo.setTitulo("Logo " + System.currentTimeMillis());
            nuevoLogo.setActivo(false);

            Logo guardado = logoService.guardar(nuevoLogo);
            return ResponseEntity.ok(guardado);
        } catch (IOException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/{id}/activar")
    public ResponseEntity<?> activar(@PathVariable Long id) {
        try {
            logoService.activar(id);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Logo activado correctamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            Optional<Logo> logoOpt = logoService.obtenerPorId(id);
            if (logoOpt.isPresent()) {
                Logo logo = logoOpt.get();
                if (logo.getImagenUrl() != null) {
                    String rutaRelativa = logo.getImagenUrl().replace("/imagenes/", "");
                    Path rutaArchivo = Paths.get(uploadDir, rutaRelativa);
                    Files.deleteIfExists(rutaArchivo);
                }
            }
            logoService.eliminar(id);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Logo eliminado correctamente");
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}