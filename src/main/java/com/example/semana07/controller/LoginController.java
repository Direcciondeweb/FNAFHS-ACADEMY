package com.example.semana07.controller;

import com.example.semana07.dto.LoginPaso1DTO;
import com.example.semana07.dto.LoginPaso2DTO;
import com.example.semana07.entity.Usuario;
import com.example.semana07.repository.UsuarioRepository;
import com.example.semana07.security.UsuarioPrincipal;
import com.example.semana07.service.CodigoVerificacionService;
import com.example.semana07.service.HistorialService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private CodigoVerificacionService codigoVerificacionService;
    @Autowired private HistorialService historialService;

    /** Paso 1: valida usuario/contraseña. Si son correctos, envía código al correo. */
    @PostMapping("/login-paso1")
    public ResponseEntity<?> loginPaso1(@Valid @RequestBody LoginPaso1DTO dto) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(dto.getUsername());

        if (usuarioOpt.isEmpty() || !passwordEncoder.matches(dto.getPassword(), usuarioOpt.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Usuario o contraseña incorrectos"));
        }

        Usuario usuario = usuarioOpt.get();
        if (usuario.getEstado() == null || usuario.getEstado() != 1) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Esta cuenta está inactiva"));
        }
        if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Esta cuenta no tiene un correo registrado. Contacta al administrador."));
        }

        var bloqueo = codigoVerificacionService.consultarBloqueo(usuario.getEmail());
        if (bloqueo.bloqueado()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of(
                    "error", "Demasiados intentos fallidos. Intenta más tarde.",
                    "segundosRestantes", bloqueo.segundosRestantes()
            ));
        }

        codigoVerificacionService.solicitarCodigo(usuario.getEmail());

        String emailOculto = ocultarEmail(usuario.getEmail());
        return ResponseEntity.ok(Map.of("message", "Código enviado", "emailOculto", emailOculto));
    }

    /** Paso 2: verifica el código y, si es correcto, autentica realmente al usuario. */
    @PostMapping("/login-paso2")
    public ResponseEntity<?> loginPaso2(@Valid @RequestBody LoginPaso2DTO dto, HttpServletRequest request) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(dto.getUsername());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Sesión inválida, vuelve a intentar el login."));
        }
        Usuario usuario = usuarioOpt.get();

        var resultado = codigoVerificacionService.verificarCodigo(usuario.getEmail(), dto.getCodigo());

        if (resultado.isBloqueado()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of(
                    "error", resultado.getMensaje(),
                    "segundosRestantes", resultado.getSegundosBloqueo()
            ));
        }
        if (!resultado.isExito()) {
            return ResponseEntity.badRequest().body(Map.of("error", resultado.getMensaje()));
        }

        // Código correcto: autenticamos manualmente y creamos la sesión real.
        UsuarioPrincipal principal = new UsuarioPrincipal(usuario);
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authToken);
        SecurityContextHolder.setContext(context);

        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
        session.setAttribute("usuario", usuario); // compatibilidad con controllers existentes

        historialService.registrar(usuario.getUsername(), usuario.getRol(), "LOGIN", "Usuario",
                usuario.getUsername(), "Inicio de sesión verificado con código");

        String destino = switch (usuario.getRol()) {
            case "ADMIN" -> "/dashboard/admin";
            case "SUBADMIN" -> "/dashboard/subadmin";
            default -> "/dashboard/user";
        };

        return ResponseEntity.ok(Map.of("message", "Sesión iniciada correctamente", "redirect", destino));
    }

    /** Reenviar código durante el login (mismo mecanismo, sin límite de intentos de envío). */
    @PostMapping("/login-reenviar")
    public ResponseEntity<?> reenviar(@Valid @RequestBody LoginPaso1DTO dto) {
        return loginPaso1(dto); // reutiliza la misma validación + reenvío
    }

    private String ocultarEmail(String email) {
        int arroba = email.indexOf('@');
        if (arroba <= 1) return email;
        String usuarioParte = email.substring(0, arroba);
        String visible = usuarioParte.substring(0, Math.min(2, usuarioParte.length()));
        return visible + "***" + email.substring(arroba);
    }
}