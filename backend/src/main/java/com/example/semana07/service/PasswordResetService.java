package com.example.semana07.service;

import com.example.semana07.entity.PasswordResetToken;
import com.example.semana07.entity.Usuario;
import com.example.semana07.repository.PasswordResetTokenRepository;
import com.example.semana07.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PasswordResetTokenRepository tokenRepository;
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;
    
    public boolean sendResetEmail(String email) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isEmpty()) {
            return false;
        }
        
        Usuario usuario = usuarioOpt.get();
        
        // Invalidar tokens anteriores
        tokenRepository.deleteByUsuario(usuario);
        
        // Crear nuevo token
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUsuario(usuario);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(24));
        resetToken.setUsed(false);
        tokenRepository.save(resetToken);
        
        // Enviar email
        String resetUrl = baseUrl + "/reset-password?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(usuario.getEmail());
        message.setSubject("FNAFHS Academy - Recuperación de Contraseña");
        message.setText("Hola " + (usuario.getNombreCompleto() != null ? usuario.getNombreCompleto() : usuario.getUsername()) + ",\n\n" +
                       "Has solicitado restablecer tu contraseña.\n\n" +
                       "Haz clic en el siguiente enlace para crear una nueva contraseña:\n" +
                       resetUrl + "\n\n" +
                       "Este enlace expirará en 24 horas.\n\n" +
                       "Si no solicitaste este cambio, ignora este mensaje.\n\n" +
                       "★ FNAFHS ACADEMY ★");
        
        mailSender.send(message);
        return true;
    }
    
    public boolean resetPassword(String token, String newPassword) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByTokenAndUsedFalse(token);
        
        if (tokenOpt.isEmpty()) {
            return false;
        }
        
        PasswordResetToken resetToken = tokenOpt.get();
        
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return false;
        }
        
        Usuario usuario = resetToken.getUsuario();
        usuario.setPassword(newPassword);
        usuarioRepository.save(usuario);
        
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
        
        return true;
    }
}