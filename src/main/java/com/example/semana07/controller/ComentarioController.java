package com.example.semana07.controller;

import com.example.semana07.dto.ComentarioCreateDTO;
import com.example.semana07.entity.Comentario;
import com.example.semana07.entity.Usuario;
import com.example.semana07.service.ComentarioService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comentarios")
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
                                   @Valid @RequestBody ComentarioCreateDTO dto, HttpSession session) {
        Usuario usuario = usuarioDeSesion(session);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Debes iniciar sesión para comentar"));
        }
        Comentario creado = comentarioService.crear(tipo.toUpperCase(), contenidoId, usuario.getUsername(), usuario.getRol(), dto.getTexto());
        return ResponseEntity.ok(creado);
    }

    // ----- Endpoints de administración (moderación) -----

    @GetMapping("/admin/todos")
    public ResponseEntity<Page<Comentario>> listarTodosAdmin(Pageable pageable) {
        return ResponseEntity.ok(comentarioService.listarTodosAdmin(pageable));
    }

    @PutMapping("/admin/{id}/fijar")
    public ResponseEntity<?> fijar(@PathVariable Long id, @RequestParam boolean valor, HttpSession session) {
        Usuario admin = usuarioDeSesion(session);
        comentarioService.fijar(id, valor, admin != null ? admin.getUsername() : "admin", admin != null ? admin.getRol() : "ADMIN");
        return ResponseEntity.ok(Map.of("mensaje", "Actualizado"));
    }

    @PutMapping("/admin/{id}/censurar")
    public ResponseEntity<?> censurar(@PathVariable Long id, @RequestParam boolean valor, HttpSession session) {
        Usuario admin = usuarioDeSesion(session);
        comentarioService.censurar(id, valor, admin != null ? admin.getUsername() : "admin", admin != null ? admin.getRol() : "ADMIN");
        return ResponseEntity.ok(Map.of("mensaje", "Actualizado"));
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<?> eliminarAdmin(@PathVariable Long id, HttpSession session) {
        Usuario admin = usuarioDeSesion(session);
        comentarioService.eliminar(id, admin != null ? admin.getUsername() : "admin", admin != null ? admin.getRol() : "ADMIN");
        return ResponseEntity.ok(Map.of("mensaje", "Comentario eliminado"));
    }

    private Usuario usuarioDeSesion(HttpSession session) {
        Object obj = session.getAttribute("usuario");
        return obj instanceof Usuario ? (Usuario) obj : null;
    }
}