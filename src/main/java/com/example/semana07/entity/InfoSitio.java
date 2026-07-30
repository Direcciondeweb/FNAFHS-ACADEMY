package com.example.semana07.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "info_sitio")
public class InfoSitio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String vision;

    @Column(columnDefinition = "TEXT")
    private String mision;

    private LocalDateTime fechaActualizacion = LocalDateTime.now();

    public InfoSitio() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getVision() { return vision; }
    public void setVision(String vision) { this.vision = vision; }
    public String getMision() { return mision; }
    public void setMision(String mision) { this.mision = mision; }
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
}