package com.example.semana07.controller;

import com.example.semana07.dto.RegistroDTO;
import com.example.semana07.dto.UsuarioCreateDTO;
import com.example.semana07.entity.Usuario;
import com.example.semana07.repository.UsuarioRepository;
import com.example.semana07.service.EmailService;
import com.example.semana07.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
public class RegistroController {

    @Autowired private UsuarioService usuarioService;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private EmailService emailService;

    @GetMapping("/registro")
    public String showRegistro() {
        return "registro";
    }

    @PostMapping("/api/auth/registro")
    @ResponseBody
    public ResponseEntity<?> registrar(@Valid @RequestBody RegistroDTO dto) {
        Map<String, String> errores = new HashMap<>();

        if (!dto.getPassword().equals(dto.getConfirmarPassword())) {
            errores.put("confirmarPassword", "Las contraseñas no coinciden");
        }
        if (usuarioRepository.existsByUsername(dto.getUsername())) {
            errores.put("username", "Ese nombre de usuario ya está en uso");
        }
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            errores.put("email", "Ese correo ya está registrado");
        }

        if (!errores.isEmpty()) {
            Map<String, Object> body = new HashMap<>();
            body.put("error", "Datos inválidos");
            body.put("detalles", errores);
            return ResponseEntity.badRequest().body(body);
        }

        UsuarioCreateDTO createDto = new UsuarioCreateDTO();
        createDto.setUsername(dto.getUsername());
        createDto.setPassword(dto.getPassword());
        createDto.setNombreCompleto(dto.getNombreCompleto());
        createDto.setEmail(dto.getEmail());
        createDto.setRol("USER"); // el registro público SIEMPRE crea usuarios normales

        Usuario creado = usuarioService.crearUsuario(createDto);

        emailService.sendWelcomeEmail(creado.getEmail(),
                creado.getNombreCompleto() != null ? creado.getNombreCompleto() : creado.getUsername());

        return ResponseEntity.ok(Map.of("message", "Registro exitoso. Ya puedes iniciar sesión."));
    }
}