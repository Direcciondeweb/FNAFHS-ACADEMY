package com.example.semana07.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    /**
     * Envía un correo electrónico simple
     * @param to Email del destinatario
     * @param subject Asunto del correo
     * @param body Cuerpo del mensaje
     */
    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            System.out.println("✅ Email enviado a: " + to);
        } catch (Exception e) {
            System.err.println("❌ Error al enviar email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Envía un correo de bienvenida a nuevos usuarios
     * @param to Email del usuario
     * @param nombre Nombre del usuario
     */
    public void sendWelcomeEmail(String to, String nombre) {
        String subject = "🌟 Bienvenido a FNAFHS Academy 🌟";
        String body = "¡Hola " + nombre + "!\n\n" +
                      "Te damos la bienvenida a FNAFHS Academy, la comunidad más grande de fans de FNAFHS.\n\n" +
                      "Aquí podrás:\n" +
                      "• Ver y compartir arte oficial y fanarts\n" +
                      "• Explorar la galería de personajes\n" +
                      "• Leer cómics exclusivos\n" +
                      "• Ver videos de la comunidad\n\n" +
                      "¡Explora y disfruta!\n\n" +
                      "★ FNAFHS ACADEMY ★";
        sendEmail(to, subject, body);
    }

    /**
     * Envía notificación de nuevo fanart
     * @param adminEmail Email del administrador
     * @param titulo Título del fanart
     * @param autor Autor del fanart
     */
    public void sendNewFanartNotification(String adminEmail, String titulo, String autor) {
        String subject = "🎨 Nuevo Fanart en FNAFHS Academy";
        String body = "¡Se ha subido un nuevo fanart!\n\n" +
                      "Título: " + titulo + "\n" +
                      "Autor: " + autor + "\n\n" +
                      "Revisa el panel de administración para moderarlo.\n\n" +
                      "★ FNAFHS ACADEMY ★";
        sendEmail(adminEmail, subject, body);
    }

    /**
     * Envía notificación de nuevo usuario registrado
     * @param adminEmail Email del administrador
     * @param username Nombre de usuario
     * @param email Email del nuevo usuario
     */
    public void sendNewUserNotification(String adminEmail, String username, String email) {
        String subject = "👤 Nuevo usuario registrado";
        String body = "Un nuevo usuario se ha registrado en FNAFHS Academy:\n\n" +
                      "Usuario: " + username + "\n" +
                      "Email: " + email + "\n\n" +
                      "★ FNAFHS ACADEMY ★";
        sendEmail(adminEmail, subject, body);
    }

    /**
     * Envía código de verificación para recuperar contraseña
     * @param to Email del usuario
     * @param codigo Código de verificación
     */
    public void sendVerificationCode(String to, String codigo) {
        String subject = "🔐 Código de verificación - FNAFHS Academy";
        String body = "Tu código de verificación es: " + codigo + "\n\n" +
                      "Este código expirará en 10 minutos.\n\n" +
                      "Si no solicitaste este cambio, ignora este mensaje.\n\n" +
                      "★ FNAFHS ACADEMY ★";
        sendEmail(to, subject, body);
    }

    /**
     * Envía confirmación de cambio de contraseña
     * @param to Email del usuario
     * @param nombre Nombre del usuario
     */
    public void sendPasswordChangedConfirmation(String to, String nombre) {
        String subject = "🔒 Tu contraseña ha sido cambiada";
        String body = "Hola " + nombre + ",\n\n" +
                      "Te confirmamos que tu contraseña ha sido cambiada exitosamente.\n\n" +
                      "Si no realizaste este cambio, por favor contacta al soporte inmediatamente.\n\n" +
                      "★ FNAFHS ACADEMY ★";
        sendEmail(to, subject, body);
    }
}