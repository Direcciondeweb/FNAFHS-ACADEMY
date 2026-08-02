package com.example.semana07.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginPaso2DTO {
    @NotBlank private String username;
    @NotBlank private String codigo;
    @NotBlank private String email;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}