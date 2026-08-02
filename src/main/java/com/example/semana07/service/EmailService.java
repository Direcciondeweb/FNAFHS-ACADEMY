package com.example.semana07.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${email.from:dianix970@gmail.com}")
    private String fromEmail;

    private void sendEmail(String to, String subject, String body) {
        log.info("=== Intento de envío ===");
        log.info("mailSender configurado: {}", mailSender != null);
        log.info("from: {}", fromEmail);
        log.info("to: {}", to);

        if (mailSender == null) {
            log.error("JavaMailSender es NULL. Revisa que spring-boot-starter-mail esté en el pom.xml " +
                    "y que spring.mail.host esté definido en application.properties.");
            return;
        }

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, false);

            mailSender.send(mimeMessage);
            log.info("Email ENVIADO exitosamente a: {} desde: {}", to, fromEmail);

        } catch (MessagingException e) {
            log.error("MessagingException al enviar a {}: {}", to, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error inesperado al enviar email a {}: {}", to, e.getMessage(), e);
            if (e.getCause() != null) {
                log.error("Causa raíz: {}", e.getCause().getMessage());
            }
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