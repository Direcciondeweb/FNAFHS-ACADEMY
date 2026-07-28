package com.example.semana07.controller;

import com.example.semana07.entity.Usuario;
import com.example.semana07.entity.Video;
import com.example.semana07.service.VideoService;
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
@RequestMapping("/api/videos")
@CrossOrigin(origins = "*")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @Value("${file.upload-dir:./imagenes/}")
    private String uploadDir;

    private static final String CARPETA = "videos";

    @GetMapping
    public ResponseEntity<List<Video>> listar() {
        return ResponseEntity.ok(videoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Video> obtener(@PathVariable Long id) {
        return videoService.obtenerPorId(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crear(
            @RequestParam("titulo") String titulo,
            @RequestParam("videoFile") MultipartFile videoFile,
            HttpSession session) {
        try {
            if (videoFile.isEmpty()) {
                return ResponseEntity.badRequest().body("No se seleccionó ningún video");
            }

            Path rutaCarpeta = Paths.get(uploadDir, CARPETA);
            if (!Files.exists(rutaCarpeta)) Files.createDirectories(rutaCarpeta);

            String extension = "";
            String nombreOriginal = videoFile.getOriginalFilename();
            if (nombreOriginal != null && nombreOriginal.contains(".")) {
                extension = nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
            }
            String nombreArchivo = UUID.randomUUID().toString() + extension;
            Files.write(Paths.get(uploadDir, CARPETA, nombreArchivo), videoFile.getBytes());

            Video nuevoVideo = new Video();
            nuevoVideo.setTitulo(titulo);
            nuevoVideo.setVideoUrl("/imagenes/" + CARPETA + "/" + nombreArchivo);
            nuevoVideo.setEstado(1);

            Map<String, String> sesion = datosSesion(session);
            Video guardado = videoService.guardar(nuevoVideo, sesion.get("usuario"), sesion.get("rol"));
            return ResponseEntity.ok(guardado);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, HttpSession session) {
        Video video = videoService.obtenerPorId(id).orElse(null);
        if (video != null && video.getVideoUrl() != null) {
            try {
                String rutaRelativa = video.getVideoUrl().replace("/imagenes/", "");
                Files.deleteIfExists(Paths.get(uploadDir, rutaRelativa));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Map<String, String> sesion = datosSesion(session);
        videoService.eliminar(id, sesion.get("usuario"), sesion.get("rol"));
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
}