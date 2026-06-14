package com.example.semana07.controller;

import com.example.semana07.entity.Usuario;
import com.example.semana07.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<Usuario>> listar() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtener(@PathVariable Long id) {
        return usuarioService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/contar-admins")
    public ResponseEntity<?> contarAdmins() {
        return ResponseEntity.ok(Map.of("totalAdmins", usuarioService.contarAdmins()));
    }

    @GetMapping("/contar-subadmins")
    public ResponseEntity<?> contarSubadmins() {
        return ResponseEntity.ok(Map.of("totalSubadmins", usuarioService.contarSubadmins()));
    }

    @GetMapping("/contar-users")
    public ResponseEntity<?> contarUsers() {
        return ResponseEntity.ok(Map.of("totalUsers", usuarioService.contarUsers()));
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Usuario usuario) {
        try {
            if (usuario.getEstado() == null) {
                usuario.setEstado(1);
            }
            Usuario nuevoUsuario = usuarioService.guardar(usuario);
            return ResponseEntity.ok(nuevoUsuario);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Usuario usuario) {
        try {
            usuarioService.actualizarUsuario(id, usuario);
            return ResponseEntity.ok(Map.of("mensaje", "Usuario actualizado correctamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstado(@PathVariable Long id, @RequestParam Integer estado) {
        try {
            usuarioService.actualizarEstado(id, estado);
            return ResponseEntity.ok(Map.of("mensaje", "Estado actualizado"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/{id}/rol")
    public ResponseEntity<?> actualizarRol(@PathVariable Long id, @RequestParam String rol) {
        try {
            Usuario usuario = usuarioService.actualizarRol(id, rol);
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}/permisos-subadmin")
    public ResponseEntity<?> actualizarPermisosSubadmin(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Boolean> permisosSubadmin = (Map<String, Boolean>) body.get("permisosSubadmin");
            Usuario usuario = usuarioService.actualizarPermisosSubadmin(id, permisosSubadmin);
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}/permisos")
    public ResponseEntity<?> actualizarPermisos(@PathVariable Long id, @RequestBody Map<String, Set<String>> body) {
        try {
            Usuario usuario = usuarioService.actualizarPermisos(id, body.get("permisos"));
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            usuarioService.eliminar(id);
            return ResponseEntity.ok().body(Map.of("mensaje", "Usuario eliminado correctamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}