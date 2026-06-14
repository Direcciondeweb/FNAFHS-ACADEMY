package com.example.semana07.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "personajes")
public class Personaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 50)
    private String categoria;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(length = 500)
    private String imagenUrl;

    @Column(length = 500)
    private String imagenOriginalUrl;

    private Integer estado = 1;

    private LocalDateTime fechaRegistro = LocalDateTime.now();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "personaje_galeria", joinColumns = @JoinColumn(name = "personaje_id"))
    @Column(name = "galeria_url")
    private List<String> galeriaImagenes = new ArrayList<>();

    public Personaje() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    public String getImagenOriginalUrl() { return imagenOriginalUrl; }
    public void setImagenOriginalUrl(String imagenOriginalUrl) { this.imagenOriginalUrl = imagenOriginalUrl; }
    public Integer getEstado() { return estado; }
    public void setEstado(Integer estado) { this.estado = estado; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    public List<String> getGaleriaImagenes() { return galeriaImagenes; }
    public void setGaleriaImagenes(List<String> galeriaImagenes) { this.galeriaImagenes = galeriaImagenes; }
}