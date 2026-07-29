package com.example.semana07.security;

import com.example.semana07.entity.Usuario;
import com.example.semana07.service.HistorialService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private HistorialService historialService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        UsuarioPrincipal principal = (UsuarioPrincipal) authentication.getPrincipal();
        Usuario usuario = principal.getUsuario();

        // Compatibilidad: los controllers existentes leen session.getAttribute("usuario")
        HttpSession session = request.getSession();
        session.setAttribute("usuario", usuario);

        historialService.registrar(usuario.getUsername(), usuario.getRol(), "LOGIN", "Usuario",
                usuario.getUsername(), "Inicio de sesión");

        String destino = switch (usuario.getRol()) {
            case "ADMIN" -> "/dashboard/admin";
            case "SUBADMIN" -> "/dashboard/subadmin";
            default -> "/dashboard/user";
        };
        response.sendRedirect(destino);
    }
}