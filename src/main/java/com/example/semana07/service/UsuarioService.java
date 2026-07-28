package com.example.semana07.service;

import com.example.semana07.entity.Usuario;
import com.example.semana07.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private HistorialService historialService;

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> obtenerPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> autenticar(String username, String password) {
        Optional<Usuario> usuario = usuarioRepository.findByUsername(username);
        if (usuario.isPresent()
                && passwordEncoder.matches(password, usuario.get().getPassword())
                && usuario.get().getEstado() == 1) {
            historialService.registrar(username, usuario.get().getRol(), "LOGIN", "Usuario", username, "Inicio de sesión");
            return usuario;
        }
        return Optional.empty();
    }

    public Usuario guardar(Usuario usuario) {
        if ("ADMIN".equals(usuario.getRol()) && contarAdmins() >= 1) {
            throw new RuntimeException("Solo puede existir un administrador en el sistema");
        }
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        Usuario guardado = usuarioRepository.save(usuario);
        historialService.registrar(usuario.getUsername(), usuario.getRol(), "CREAR", "Usuario",
                String.valueOf(guardado.getId()), "Usuario registrado");
        return guardado;
    }

    public Usuario actualizarUsuario(Long id, Usuario usuarioActualizado) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setNombreCompleto(usuarioActualizado.getNombreCompleto());
        usuario.setEmail(usuarioActualizado.getEmail());
        usuario.setTelefono(usuarioActualizado.getTelefono());
        usuario.setDireccion(usuarioActualizado.getDireccion());

        if (usuarioActualizado.getPassword() != null && !usuarioActualizado.getPassword().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(usuarioActualizado.getPassword()));
        }

        if (usuarioActualizado.getRol() != null) {
            if ("ADMIN".equals(usuarioActualizado.getRol()) && contarAdmins() >= 1 && !"ADMIN".equals(usuario.getRol())) {
                throw new RuntimeException("Solo puede existir un administrador");
            }
            usuario.setRol(usuarioActualizado.getRol());
        }

        if (usuarioActualizado.getEstado() != null) {
            usuario.setEstado(usuarioActualizado.getEstado());
        }

        Usuario actualizado = usuarioRepository.save(usuario);
        historialService.registrar(usuario.getUsername(), usuario.getRol(), "ACTUALIZAR", "Usuario",
                String.valueOf(id), "Perfil actualizado");
        return actualizado;
    }

    public void actualizarEstado(Long id, Integer estado) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setEstado(estado);
        usuarioRepository.save(usuario);
        historialService.registrar(usuario.getUsername(), usuario.getRol(), "ACTUALIZAR", "Usuario",
                String.valueOf(id), "Estado cambiado a " + estado);
    }

    public Usuario actualizarRol(Long id, String rol) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if ("ADMIN".equals(rol) && contarAdmins() >= 1 && !"ADMIN".equals(usuario.getRol())) {
            throw new RuntimeException("Solo puede existir un administrador");
        }
        usuario.setRol(rol);
        Usuario actualizado = usuarioRepository.save(usuario);
        historialService.registrar(usuario.getUsername(), rol, "ACTUALIZAR", "Usuario",
                String.valueOf(id), "Rol cambiado a " + rol);
        return actualizado;
    }

    public Usuario actualizarPermisosSubadmin(Long id, Map<String, Boolean> permisosSubadmin) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setPermisosSubadmin(permisosSubadmin);
        return usuarioRepository.save(usuario);
    }

    public Usuario actualizarPermisos(Long id, Set<String> permisos) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setPermisos(permisos);
        return usuarioRepository.save(usuario);
    }

    public void eliminar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if ("ADMIN".equals(usuario.getRol()) && contarAdmins() <= 1) {
            throw new RuntimeException("No se puede eliminar el único administrador");
        }
        usuarioRepository.deleteById(id);
        historialService.registrar(usuario.getUsername(), usuario.getRol(), "ELIMINAR", "Usuario",
                String.valueOf(id), "Usuario eliminado");
    }

    public long contarAdmins() { return usuarioRepository.countByRol("ADMIN"); }
    public long contarSubadmins() { return usuarioRepository.countByRol("SUBADMIN"); }
    public long contarUsers() { return usuarioRepository.countByRol("USER"); }
}