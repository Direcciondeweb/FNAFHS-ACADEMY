package com.example.semana07.controller;

import com.example.semana07.entity.Usuario;
import com.example.semana07.service.PasswordResetService;
import com.example.semana07.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@Controller
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private PasswordResetService passwordResetService;

    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        var usuarioOpt = usuarioService.autenticar(username, password);
        if (usuarioOpt.isEmpty()) {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
            return "login";
        } else {
            Usuario usuario = usuarioOpt.get();
            session.setAttribute("usuario", usuario);
            if ("ADMIN".equals(usuario.getRol())) {
                return "redirect:/dashboard/admin";
            } else if ("SUBADMIN".equals(usuario.getRol())) {
                return "redirect:/dashboard/subadmin";
            } else {
                return "redirect:/dashboard/user";
            }
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
    
    // API endpoints para recuperación de contraseña (JSON)
    @PostMapping("/api/auth/forgot-password")
    @ResponseBody
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        boolean sent = passwordResetService.sendResetEmail(email);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Si el email existe, recibirás instrucciones para recuperar tu contraseña");
        response.put("sent", sent);
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/api/auth/reset-password")
    @ResponseBody
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");
        
        boolean success = passwordResetService.resetPassword(token, newPassword);
        
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Contraseña actualizada correctamente"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "Token inválido o expirado"));
        }
    }
}