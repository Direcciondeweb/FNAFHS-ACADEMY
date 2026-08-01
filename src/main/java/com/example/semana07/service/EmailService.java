package com.example.semana07.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    private void sendEmail(String to, String subject, String body) {
        if (mailSender == null) {
            log.warn("Mail no configurado, no se envió el correo a: {}", to);
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Email enviado a: {}", to);
        } catch (Exception e) {
            log.error("Error al enviar email a {}: {}", to, e.getMessage());
        }
    }

    @Async
    public void sendWelcomeEmail(String to, String nombre) {
        String subject = "🌟 Bienvenido a FNAFHS Academy 🌟";
        String body = "¡Hola " + nombre + "!\n\n" +
                "Te damos la bienvenida a FNAFHS Academy, la comunidad más grande de fans de FNAFHS.\n\n" +
                "★ FNAFHS ACADEMY ★";
        sendEmail(to, subject, body);
    }

    @Async
    public void sendNewFanartNotification(String adminEmail, String titulo, String autor) {
        String subject = "🎨 Nuevo Fanart en FNAFHS Academy";
        String body = "¡Se ha subido un nuevo fanart!\n\nTítulo: " + titulo + "\nAutor: " + autor + "\n\n★ FNAFHS ACADEMY ★";
        sendEmail(adminEmail, subject, body);
    }

    @Async
    public void sendNewUserNotification(String adminEmail, String username, String email) {
        String subject = "👤 Nuevo usuario registrado";
        String body = "Un nuevo usuario se ha registrado:\n\nUsuario: " + username + "\nEmail: " + email + "\n\n★ FNAFHS ACADEMY ★";
        sendEmail(adminEmail, subject, body);
    }

    @Async
    public void sendVerificationCode(String to, String codigo) {
        String subject = "🔐 Código de verificación - FNAFHS Academy";
        String body = "Tu código de verificación es: " + codigo + "\n\n" +
                "Este código expirará en 15 minutos.\n\n" +
                "Si no solicitaste este código, ignora este mensaje.\n\n" +
                "★ FNAFHS ACADEMY ★";
        sendEmail(to, subject, body);
    }

    @Async
    public void sendPasswordChangedConfirmation(String to, String nombre) {
        String subject = "🔒 Tu contraseña ha sido cambiada";
        String body = "Hola " + nombre + ",\n\nTe confirmamos que tu contraseña ha sido cambiada exitosamente.\n\n★ FNAFHS ACADEMY ★";
        sendEmail(to, subject, body);
    }
}