package com.example.semana07.dto;

import jakarta.validation.constraints.Email;

public class UsuarioUpdateDTO {

    private String nombreCompleto;

    @Email(message = "El email no tiene un formato válido")
    private String email;

    private String telefono;
    private String direccion;
    private String password; // opcional, solo si se quiere cambiar

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}