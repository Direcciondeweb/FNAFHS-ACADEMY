package com.example.semana07.controller;

import com.example.semana07.dto.ReporteCreateDTO;
import com.example.semana07.entity.Reporte;
import com.example.semana07.entity.Usuario;
import com.example.semana07.service.ReporteService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    @PostMapping("/{tipo}/{contenidoId}")
    public ResponseEntity<?> crear(@PathVariable String tipo, @PathVariable Long contenidoId,
                                   @Valid @RequestBody ReporteCreateDTO dto, HttpSession session) {
        Usuario usuario = usuarioDeSesion(session);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Debes iniciar sesión para reportar"));
        }
        Reporte creado = reporteService.crear(tipo.toUpperCase(), contenidoId, usuario.getUsername(), dto.getMotivo(), dto.getDescripcion());
        return ResponseEntity.ok(creado);
    }

    @GetMapping("/admin/pendientes")
    public ResponseEntity<Page<Reporte>> pendientes(Pageable pageable) {
        return ResponseEntity.ok(reporteService.listarPendientes(pageable));
    }

    @GetMapping("/admin")
    public ResponseEntity<Page<Reporte>> todos(Pageable pageable) {
        return ResponseEntity.ok(reporteService.listarTodos(pageable));
    }

    @GetMapping("/admin/contar-pendientes")
    public ResponseEntity<?> contarPendientes() {
        return ResponseEntity.ok(Map.of("total", reporteService.contarPendientes()));
    }

    @PutMapping("/admin/{id}/resolver")
    public ResponseEntity<?> resolver(@PathVariable Long id, HttpSession session) {
        Usuario usuario = usuarioDeSesion(session);
        Reporte actualizado = reporteService.resolver(id,
                usuario != null ? usuario.getUsername() : "sistema",
                usuario != null ? usuario.getRol() : "ADMIN");
        return ResponseEntity.ok(actualizado);
    }

    @PutMapping("/admin/{id}/rechazar")
    public ResponseEntity<?> rechazar(@PathVariable Long id, HttpSession session) {
        Usuario usuario = usuarioDeSesion(session);
        Reporte actualizado = reporteService.rechazar(id,
                usuario != null ? usuario.getUsername() : "sistema",
                usuario != null ? usuario.getRol() : "ADMIN");
        return ResponseEntity.ok(actualizado);
    }

    private Usuario usuarioDeSesion(HttpSession session) {
        Object obj = session.getAttribute("usuario");
        return obj instanceof Usuario ? (Usuario) obj : null;
    }
}