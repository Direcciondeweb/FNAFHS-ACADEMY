package com.example.semana07.controller;

import com.example.semana07.dto.CambiarPasswordConCodigoDTO;
import com.example.semana07.dto.SolicitarCodigoDTO;
import com.example.semana07.dto.VerificarCodigoDTO;
import com.example.semana07.service.CodigoVerificacionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
public class AuthController {

    @Autowired
    private CodigoVerificacionService codigoVerificacionService;

    @GetMapping("/login")
    public String showLogin(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            Model model) {
        if (error != null) {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
        }
        if (logout != null) {
            model.addAttribute("success", "Sesión cerrada correctamente");
        }
        return "login";
    }

    @PostMapping("/api/auth/solicitar-codigo")
    @ResponseBody
    public ResponseEntity<?> solicitarCodigo(@Valid @RequestBody SolicitarCodigoDTO dto) {
        var bloqueo = codigoVerificacionService.consultarBloqueo(dto.getEmail());
        if (bloqueo.bloqueado()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of(
                    "error", "Cuenta bloqueada temporalmente por intentos fallidos.",
                    "segundosRestantes", bloqueo.segundosRestantes()
            ));
        }

        codigoVerificacionService.solicitarCodigo(dto.getEmail());
        // Mensaje genérico: no confirmamos si el correo existe o no (seguridad)
        return ResponseEntity.ok(Map.of(
                "message", "Si el correo está registrado, recibirás un código de verificación."
        ));
    }

    @PostMapping("/api/auth/verificar-codigo")
    @ResponseBody
    public ResponseEntity<?> verificarCodigo(@Valid @RequestBody VerificarCodigoDTO dto) {
        var resultado = codigoVerificacionService.verificarCodigo(dto.getEmail(), dto.getCodigo());

        if (resultado.isBloqueado()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of(
                    "error", resultado.getMensaje(),
                    "segundosRestantes", resultado.getSegundosBloqueo()
            ));
        }
        if (!resultado.isExito()) {
            return ResponseEntity.badRequest().body(Map.of("error", resultado.getMensaje()));
        }
        return ResponseEntity.ok(Map.of("message", "Código verificado correctamente"));
    }

    @PostMapping("/api/auth/cambiar-password")
    @ResponseBody
    public ResponseEntity<?> cambiarPassword(@Valid @RequestBody CambiarPasswordConCodigoDTO dto) {
        var resultado = codigoVerificacionService.cambiarPasswordConCodigo(
                dto.getEmail(), dto.getCodigo(), dto.getNuevaPassword());

        if (resultado.isBloqueado()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of(
                    "error", resultado.getMensaje(),
                    "segundosRestantes", resultado.getSegundosBloqueo()
            ));
        }
        if (!resultado.isExito()) {
            return ResponseEntity.badRequest().body(Map.of("error", resultado.getMensaje()));
        }
        return ResponseEntity.ok(Map.of("message", "Contraseña actualizada correctamente"));
    }
}