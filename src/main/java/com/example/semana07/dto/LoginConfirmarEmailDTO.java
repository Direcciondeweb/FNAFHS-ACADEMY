package com.example.semana07.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginConfirmarEmailDTO {

    @NotBlank private String username;

    @NotBlank
    @Email(message = "Ingresa un correo válido")
    private String email;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}