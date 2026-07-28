package com.example.semana07.controller;

import com.example.semana07.entity.Usuario;
import com.example.semana07.service.LikeContenidoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/likes")
@CrossOrigin(origins = "*")
public class LikeContenidoController {

    @Autowired
    private LikeContenidoService likeService;

    @PostMapping("/{tipo}/{contenidoId}/toggle")
    public ResponseEntity<?> toggle(@PathVariable String tipo, @PathVariable Long contenidoId, HttpSession session) {
        Object usuarioObj = session.getAttribute("usuario");
        if (!(usuarioObj instanceof Usuario usuario)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Debes iniciar sesión para dar like"));
        }

        String tipoUpper = tipo.toUpperCase();
        boolean leGusta = likeService.toggle(tipoUpper, contenidoId, usuario.getUsername(), usuario.getRol());
        long total = likeService.contarLikes(tipoUpper, contenidoId);

        return ResponseEntity.ok(Map.of("leGusta", leGusta, "totalLikes", total));
    }

    @GetMapping("/{tipo}/{contenidoId}/count")
    public ResponseEntity<?> contar(@PathVariable String tipo, @PathVariable Long contenidoId) {
        return ResponseEntity.ok(Map.of("totalLikes", likeService.contarLikes(tipo.toUpperCase(), contenidoId)));
    }

    @GetMapping("/{tipo}/{contenidoId}/check")
    public ResponseEntity<?> check(@PathVariable String tipo, @PathVariable Long contenidoId, HttpSession session) {
        Object usuarioObj = session.getAttribute("usuario");
        if (!(usuarioObj instanceof Usuario usuario)) {
            return ResponseEntity.ok(Map.of("leGusta", false, "totalLikes", likeService.contarLikes(tipo.toUpperCase(), contenidoId)));
        }
        boolean leGusta = likeService.usuarioLeGusta(tipo.toUpperCase(), contenidoId, usuario.getUsername());
        long total = likeService.contarLikes(tipo.toUpperCase(), contenidoId);
        return ResponseEntity.ok(Map.of("leGusta", leGusta, "totalLikes", total));
    }
}