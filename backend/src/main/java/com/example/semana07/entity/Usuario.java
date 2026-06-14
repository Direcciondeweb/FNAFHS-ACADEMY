package com.example.semana07.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 20)
    private String rol = "USER";

    @Column(length = 100)
    private String nombreCompleto;

    @Column(length = 100)
    private String email;

    @Column(length = 20)
    private String telefono;

    @Column(length = 255)
    private String direccion;

    private Integer estado = 1;

    private LocalDateTime fechaRegistro = LocalDateTime.now();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "usuario_permisos", joinColumns = @JoinColumn(name = "usuario_id"))
    @Column(name = "permiso")
    private Set<String> permisos = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "usuario_permisos_subadmin", joinColumns = @JoinColumn(name = "usuario_id"))
    @MapKeyColumn(name = "permiso_key")
    @Column(name = "permiso_value")
    private Map<String, Boolean> permisosSubadmin = new HashMap<>();

    public Usuario() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public Integer getEstado() { return estado; }
    public void setEstado(Integer estado) { this.estado = estado; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    public Set<String> getPermisos() { return permisos; }
    public void setPermisos(Set<String> permisos) { this.permisos = permisos; }
    public Map<String, Boolean> getPermisosSubadmin() { return permisosSubadmin; }
    public void setPermisosSubadmin(Map<String, Boolean> permisosSubadmin) { this.permisosSubadmin = permisosSubadmin; }
}