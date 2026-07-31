package com.example.semana07.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comentarios")
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String contenidoTipo;

    @Column(nullable = false)
    private Long contenidoId;

    @Column(nullable = false, length = 50)
    private String usuario;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String texto;

    private boolean aprobado = true;
    private boolean fijado = false;
    private boolean censurado = false;

    private LocalDateTime fecha = LocalDateTime.now();

    public Comentario() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getContenidoTipo() { return contenidoTipo; }
    public void setContenidoTipo(String contenidoTipo) { this.contenidoTipo = contenidoTipo; }
    public Long getContenidoId() { return contenidoId; }
    public void setContenidoId(Long contenidoId) { this.contenidoId = contenidoId; }
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public boolean isAprobado() { return aprobado; }
    public void setAprobado(boolean aprobado) { this.aprobado = aprobado; }
    public boolean isFijado() { return fijado; }
    public void setFijado(boolean fijado) { this.fijado = fijado; }
    public boolean isCensurado() { return censurado; }
    public void setCensurado(boolean censurado) { this.censurado = censurado; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}