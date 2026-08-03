package com.example.semana07.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private static final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";

    // API Key de Brevo (Settings -> SMTP & API -> pestaña "API Keys"), NO la SMTP key/password.
    @Value("${brevo.api-key:}")
    private String brevoApiKey;

    @Value("${email.from:dianix970@gmail.com}")
    private String fromEmail;

    @Value("${email.from-name:FNAFHS Academy}")
    private String fromName;

    private final RestTemplate restTemplate = new RestTemplate();

    private void sendEmail(String to, String subject, String body) {
        log.info("=== Intento de envío (API Brevo) === to={}, from={}", to, fromEmail);

        if (brevoApiKey == null || brevoApiKey.isBlank()) {
            log.error("brevo.api-key no está configurado. Define la variable de entorno BREVO_API_KEY.");
            return;
        }

        Map<String, Object> sender = new HashMap<>();
        sender.put("name", fromName);
        sender.put("email", fromEmail);

        Map<String, Object> destinatario = new HashMap<>();
        destinatario.put("email", to);

        Map<String, Object> payload = new HashMap<>();
        payload.put("sender", sender);
        payload.put("to", new Object[]{destinatario});
        payload.put("subject", subject);
        payload.put("textContent", body);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", brevoApiKey);
        headers.set("accept", "application/json");

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(BREVO_API_URL, request, String.class);
            HttpStatusCode status = response.getStatusCode();
            if (status.is2xxSuccessful()) {
                log.info("Email ENVIADO exitosamente a: {} (status {})", to, status.value());
            } else {
                log.error("Brevo respondió con error. status={}, body={}", status.value(), response.getBody());
            }
        } catch (RestClientException e) {
            log.error("Error al llamar a la API de Brevo para {}: {}", to, e.getMessage(), e);
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