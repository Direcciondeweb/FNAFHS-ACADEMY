package com.example.semana07.controller;

import com.example.semana07.entity.Arte;
import com.example.semana07.service.ArteService;
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
@RequestMapping("/api/arte")
@CrossOrigin(origins = "*")
public class ArteController {

    @Autowired
    private ArteService arteService;

    @Value("${file.upload-dir:./imagenes/}")
    private String uploadDir;

    @GetMapping
    public ResponseEntity<List<Arte>> listar() {
        return ResponseEntity.ok(arteService.listarTodos());
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Arte>> listarPorTipo(@PathVariable String tipo) {
        List<Arte> lista = arteService.listarPorTipo(tipo);
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/comic/{comicId}")
    public ResponseEntity<List<Arte>> listarComic(@PathVariable String comicId) {
        return ResponseEntity.ok(arteService.listarComicPorId(comicId));
    }

    @PostMapping
    public ResponseEntity<?> crear(
            @RequestParam("titulo") String titulo,
            @RequestParam("tipo") String tipo,
            @RequestParam("imagenFile") MultipartFile imagenFile,
            @RequestParam(value = "comicId", required = false) String comicId,
            @RequestParam(value = "totalPaginas", defaultValue = "1") Integer totalPaginas,
            @RequestParam(value = "autor", required = false) String autor) {
        try {
            Arte nuevoArte = new Arte();
            nuevoArte.setTitulo(titulo);
            nuevoArte.setTipo(tipo);
            nuevoArte.setComicId(comicId);
            nuevoArte.setTotalPaginas(totalPaginas);
            nuevoArte.setEstado(1);

            String carpeta;
            switch (tipo) {
                case "arte-oficial":
                    carpeta = "arte-oficial";
                    break;
                case "fanart":
                    carpeta = "fanarts";
                    break;
                case "descartado":
                    carpeta = "descartados";
                    break;
                case "comic":
                    carpeta = "comics/" + (comicId != null ? comicId : "temp");
                    break;
                default:
                    carpeta = "arte";
            }

            String imagenUrl = guardarImagen(imagenFile, carpeta);
            nuevoArte.setImagenUrl(imagenUrl);

            Arte guardado = arteService.guardar(nuevoArte);
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Arte guardado correctamente");
            response.put("arte", guardado);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            Arte arte = arteService.obtenerPorId(id).orElse(null);
            if (arte != null && arte.getImagenUrl() != null) {
                String rutaRelativa = arte.getImagenUrl().replace("/imagenes/", "");
                Path rutaArchivo = Paths.get(uploadDir, rutaRelativa);
                Files.deleteIfExists(rutaArchivo);
            }
            arteService.eliminar(id);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Arte eliminado correctamente");
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @DeleteMapping("/comic/{comicId}")
    public ResponseEntity<?> eliminarComic(@PathVariable String comicId) {
        try {
            List<Arte> paginas = arteService.listarComicPorId(comicId);
            for (Arte pagina : paginas) {
                if (pagina.getImagenUrl() != null) {
                    String rutaRelativa = pagina.getImagenUrl().replace("/imagenes/", "");
                    Path rutaArchivo = Paths.get(uploadDir, rutaRelativa);
                    Files.deleteIfExists(rutaArchivo);
                }
                arteService.eliminar(pagina.getId());
            }
            Path rutaCarpeta = Paths.get(uploadDir, "comics", comicId);
            if (Files.exists(rutaCarpeta)) {
                Files.walk(rutaCarpeta)
                        .sorted((a, b) -> b.compareTo(a))
                        .map(Path::toFile)
                        .forEach(java.io.File::delete);
            }
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Comic eliminado correctamente");
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
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
}