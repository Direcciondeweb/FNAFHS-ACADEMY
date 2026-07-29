package com.example.semana07.controller;

import com.example.semana07.entity.Notificacion;
import com.example.semana07.entity.Usuario;
import com.example.semana07.service.NotificacionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    @Autowired
    private NotificacionService notificacionService;

    @GetMapping
    public ResponseEntity<?> listar(Pageable pageable, HttpSession session) {
        Usuario usuario = usuarioDeSesion(session);
        if (usuario == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(notificacionService.listarPorUsuario(usuario.getUsername(), pageable));
    }

    @GetMapping("/no-leidas")
    public ResponseEntity<?> noLeidas(HttpSession session) {
        Usuario usuario = usuarioDeSesion(session);
        if (usuario == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        List<Notificacion> lista = notificacionService.listarNoLeidas(usuario.getUsername());
        return ResponseEntity.ok(Map.of("notificaciones", lista, "total", lista.size()));
    }

    @GetMapping("/contar")
    public ResponseEntity<?> contar(HttpSession session) {
        Usuario usuario = usuarioDeSesion(session);
        if (usuario == null) return ResponseEntity.ok(Map.of("total", 0));
        return ResponseEntity.ok(Map.of("total", notificacionService.contarNoLeidas(usuario.getUsername())));
    }

    @PutMapping("/{id}/leer")
    public ResponseEntity<?> marcarLeida(@PathVariable Long id) {
        notificacionService.marcarLeida(id);
        return ResponseEntity.ok(Map.of("mensaje", "Notificación marcada como leída"));
    }

    @PutMapping("/leer-todas")
    public ResponseEntity<?> marcarTodasLeidas(HttpSession session) {
        Usuario usuario = usuarioDeSesion(session);
        if (usuario == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        notificacionService.marcarTodasLeidas(usuario.getUsername());
        return ResponseEntity.ok(Map.of("mensaje", "Todas las notificaciones marcadas como leídas"));
    }

    private Usuario usuarioDeSesion(HttpSession session) {
        Object obj = session.getAttribute("usuario");
        return obj instanceof Usuario ? (Usuario) obj : null;
    }
}