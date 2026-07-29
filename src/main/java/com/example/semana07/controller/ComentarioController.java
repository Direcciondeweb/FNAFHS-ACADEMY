package com.example.semana07.controller;

import com.example.semana07.dto.ComentarioCreateDTO;
import com.example.semana07.entity.Comentario;
import com.example.semana07.entity.Usuario;
import com.example.semana07.service.ComentarioService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
        Object usuarioObj = session.getAttribute("usuario");
        if (!(usuarioObj instanceof Usuario usuario)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Debes iniciar sesión para comentar"));
        }
        Comentario creado = comentarioService.crear(tipo.toUpperCase(), contenidoId, usuario.getUsername(), usuario.getRol(), dto.getTexto());
        return ResponseEntity.ok(creado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id, HttpSession session) {
        Object usuarioObj = session.getAttribute("usuario");
        String usuario = usuarioObj instanceof Usuario u ? u.getUsername() : "desconocido";
        String rol = usuarioObj instanceof Usuario u ? u.getRol() : "N/A";
        comentarioService.eliminar(id, usuario, rol);
        return ResponseEntity.ok(Map.of("mensaje", "Comentario eliminado"));
    }
}