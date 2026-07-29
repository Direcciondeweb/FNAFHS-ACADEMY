package com.example.semana07.controller;

import com.example.semana07.dto.UsuarioCreateDTO;
import com.example.semana07.dto.UsuarioUpdateDTO;
import com.example.semana07.entity.Usuario;
import com.example.semana07.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<Page<Usuario>> listar(Pageable pageable) {
        return ResponseEntity.ok(usuarioService.listarPaginado(pageable));
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
    public ResponseEntity<?> crear(@Valid @RequestBody UsuarioCreateDTO dto) {
        return ResponseEntity.ok(usuarioService.crearUsuario(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioUpdateDTO dto) {
        Usuario actualizado = usuarioService.actualizarUsuario(id, dto);
        return ResponseEntity.ok(Map.of("mensaje", "Usuario actualizado correctamente", "usuario", actualizado));
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstado(@PathVariable Long id, @RequestParam Integer estado) {
        usuarioService.actualizarEstado(id, estado);
        return ResponseEntity.ok(Map.of("mensaje", "Estado actualizado"));
    }

    @PutMapping("/{id}/rol")
    public ResponseEntity<?> actualizarRol(@PathVariable Long id, @RequestParam String rol) {
        return ResponseEntity.ok(usuarioService.actualizarRol(id, rol));
    }

    @PutMapping("/{id}/permisos-subadmin")
    public ResponseEntity<?> actualizarPermisosSubadmin(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        Map<String, Boolean> permisosSubadmin = (Map<String, Boolean>) body.get("permisosSubadmin");
        return ResponseEntity.ok(usuarioService.actualizarPermisosSubadmin(id, permisosSubadmin));
    }

    @PutMapping("/{id}/permisos")
    public ResponseEntity<?> actualizarPermisos(@PathVariable Long id, @RequestBody Map<String, Set<String>> body) {
        return ResponseEntity.ok(usuarioService.actualizarPermisos(id, body.get("permisos")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.ok(Map.of("mensaje", "Usuario eliminado correctamente"));
    }
}