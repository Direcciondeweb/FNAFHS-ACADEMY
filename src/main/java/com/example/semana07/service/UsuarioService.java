package com.example.semana07.service;

import com.example.semana07.dto.UsuarioCreateDTO;
import com.example.semana07.dto.UsuarioUpdateDTO;
import com.example.semana07.entity.Usuario;
import com.example.semana07.exception.ResourceNotFoundException;
import com.example.semana07.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class UsuarioService {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private HistorialService historialService;

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Page<Usuario> listarPaginado(Pageable pageable) {
        return usuarioRepository.findAll(pageable);
    }

    public Optional<Usuario> obtenerPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Usuario crearUsuario(UsuarioCreateDTO dto) {
        if (usuarioRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("El usuario ya existe");
        }
        if ("ADMIN".equals(dto.getRol()) && contarAdmins() >= 1) {
            throw new RuntimeException("Solo puede existir un administrador en el sistema");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(dto.getUsername());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setRol(dto.getRol() != null ? dto.getRol() : "USER");
        usuario.setNombreCompleto(dto.getNombreCompleto());
        usuario.setEmail(dto.getEmail());
        usuario.setTelefono(dto.getTelefono());
        usuario.setDireccion(dto.getDireccion());
        usuario.setEstado(1);

        Usuario guardado = usuarioRepository.save(usuario);
        historialService.registrar(guardado.getUsername(), guardado.getRol(), "CREAR", "Usuario",
                String.valueOf(guardado.getId()), "Usuario creado desde el panel");
        return guardado;
    }

    public Usuario actualizarUsuario(Long id, UsuarioUpdateDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        usuario.setNombreCompleto(dto.getNombreCompleto());
        usuario.setEmail(dto.getEmail());
        usuario.setTelefono(dto.getTelefono());
        usuario.setDireccion(dto.getDireccion());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        Usuario actualizado = usuarioRepository.save(usuario);
        historialService.registrar(usuario.getUsername(), usuario.getRol(), "ACTUALIZAR", "Usuario",
                String.valueOf(id), "Perfil actualizado");
        return actualizado;
    }

    public void actualizarEstado(Long id, Integer estado) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        usuario.setEstado(estado);
        usuarioRepository.save(usuario);
        historialService.registrar(usuario.getUsername(), usuario.getRol(), "ACTUALIZAR", "Usuario",
                String.valueOf(id), "Estado cambiado a " + estado);
    }

    public Usuario actualizarRol(Long id, String rol) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
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
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        usuario.setPermisosSubadmin(permisosSubadmin);
        return usuarioRepository.save(usuario);
    }

    public Usuario actualizarPermisos(Long id, Set<String> permisos) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        usuario.setPermisos(permisos);
        return usuarioRepository.save(usuario);
    }

    public void eliminar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
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