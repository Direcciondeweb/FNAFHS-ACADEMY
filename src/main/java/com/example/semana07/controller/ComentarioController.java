package com.example.semana07.controller;

import com.example.semana07.entity.Comentario;
import com.example.semana07.entity.Usuario;
import com.example.semana07.service.ComentarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comentarios")
@CrossOrigin(origins = "*")
public class ComentarioController {

    @Autowired
    private ComentarioService comentarioService;

    @GetMapping("/{tipo}/{contenidoId}")
    public ResponseEntity<List<Comentario>> listar(@PathVariable String tipo, @PathVariable Long contenidoId) {
        return ResponseEntity.ok(comentarioService.listarPorContenido(tipo.toUpperCase(), contenidoId));
    }

    @GetMapping("/{tipo}/{contenidoId}/count")
    public ResponseEntity<?> contar(@PathVariable String tipo, @PathVariable Long contenidoId) {
        return ResponseEntity.ok(Map.of("total", comentarioService.contarPorContenido(tipo.toUpperCase(), contenidoId)));
    }

    @PostMapping("/{tipo}/{contenidoId}")
    public ResponseEntity<?> crear(@PathVariable String tipo, @PathVariable Long contenidoId,
                                   @RequestBody Map<String, String> body, HttpSession session) {
        Object usuarioObj = session.getAttribute("usuario");
        if (!(usuarioObj instanceof Usuario usuario)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Debes iniciar sesión para comentar"));
        }

        String texto = body.get("texto");
        if (texto == null || texto.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El comentario no puede estar vacío"));
        }

        Comentario creado = comentarioService.crear(tipo.toUpperCase(), contenidoId, usuario.getUsername(), usuario.getRol(), texto);
        return ResponseEntity.ok(creado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id, HttpSession session) {
        Map<String, String> sesion = datosSesion(session);
        comentarioService.eliminar(id, sesion.get("usuario"), sesion.get("rol"));
        return ResponseEntity.ok(Map.of("mensaje", "Comentario eliminado"));
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