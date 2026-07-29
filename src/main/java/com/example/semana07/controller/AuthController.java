package com.example.semana07.controller;

import com.example.semana07.dto.ForgotPasswordDTO;
import com.example.semana07.dto.ResetPasswordDTO;
import com.example.semana07.service.PasswordResetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
public class AuthController {

    @Autowired
    private PasswordResetService passwordResetService;

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

    @PostMapping("/api/auth/forgot-password")
    @ResponseBody
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordDTO dto) {
        boolean sent = passwordResetService.sendResetEmail(dto.getEmail());
        return ResponseEntity.ok(Map.of(
                "message", "Si el email existe, recibirás instrucciones para recuperar tu contraseña",
                "sent", sent
        ));
    }

    @PostMapping("/api/auth/reset-password")
    @ResponseBody
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordDTO dto) {
        boolean success = passwordResetService.resetPassword(dto.getToken(), dto.getNewPassword());
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Contraseña actualizada correctamente"));
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Token inválido o expirado"));
    }
}