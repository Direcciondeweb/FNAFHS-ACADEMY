package com.example.semana07.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginPaso2DTO {
    @NotBlank private String username;
    @NotBlank private String codigo;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
}