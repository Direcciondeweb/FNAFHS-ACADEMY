package com.example.semana07.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "arte")
public class Arte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 200)
    private String titulo;

    @Column(length = 50)
    private String tipo;

    @Column(length = 500)
    private String imagenUrl;

    @Column(length = 100)
    private String comicId;

    private Integer totalPaginas = 1;

    private Integer estado = 1;

    private LocalDateTime fechaRegistro = LocalDateTime.now();

    public Arte() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    public String getComicId() { return comicId; }
    public void setComicId(String comicId) { this.comicId = comicId; }
    public Integer getTotalPaginas() { return totalPaginas; }
    public void setTotalPaginas(Integer totalPaginas) { this.totalPaginas = totalPaginas; }
    public Integer getEstado() { return estado; }
    public void setEstado(Integer estado) { this.estado = estado; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}