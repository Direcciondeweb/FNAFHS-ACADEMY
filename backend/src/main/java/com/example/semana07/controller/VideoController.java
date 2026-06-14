package com.example.semana07.controller;

import com.example.semana07.entity.Video;
import com.example.semana07.service.VideoService;
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
        return videoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crear(
            @RequestParam("titulo") String titulo,
            @RequestParam("videoFile") MultipartFile videoFile) {
        try {
            if (videoFile.isEmpty()) {
                return ResponseEntity.badRequest().body("No se seleccionó ningún video");
            }

            Path rutaCarpeta = Paths.get(uploadDir, CARPETA);
            if (!Files.exists(rutaCarpeta)) {
                Files.createDirectories(rutaCarpeta);
            }

            String extension = "";
            String nombreOriginal = videoFile.getOriginalFilename();
            if (nombreOriginal != null && nombreOriginal.contains(".")) {
                extension = nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
            }
            String nombreArchivo = UUID.randomUUID().toString() + extension;
            Path rutaCompleta = Paths.get(uploadDir, CARPETA, nombreArchivo);
            Files.write(rutaCompleta, videoFile.getBytes());

            String videoUrl = "/imagenes/" + CARPETA + "/" + nombreArchivo;

            Video nuevoVideo = new Video();
            nuevoVideo.setTitulo(titulo);
            nuevoVideo.setVideoUrl(videoUrl);
            nuevoVideo.setEstado(1);

            Video guardado = videoService.guardar(nuevoVideo);
            return ResponseEntity.ok(guardado);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        Video video = videoService.obtenerPorId(id).orElse(null);
        if (video != null && video.getVideoUrl() != null) {
            try {
                String rutaRelativa = video.getVideoUrl().replace("/imagenes/", "");
                Path rutaArchivo = Paths.get(uploadDir, rutaRelativa);
                Files.deleteIfExists(rutaArchivo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        videoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}