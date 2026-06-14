package com.example.semana07.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "slider_images")
public class SliderImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500)
    private String imagenUrl;

    private Boolean activo = false;

    private Integer orden = 0;

    public SliderImage() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    public Integer getOrden() { return orden; }
    public void setOrden(Integer orden) { this.orden = orden; }
}