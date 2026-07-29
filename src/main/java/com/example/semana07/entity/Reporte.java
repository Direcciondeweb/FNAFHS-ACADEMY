package com.example.semana07.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reportes")
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String contenidoTipo; // ARTE, VIDEO, PERSONAJE, COMENTARIO

    @Column(nullable = false)
    private Long contenidoId;

    @Column(nullable = false, length = 50)
    private String reportadoPor;

    @Column(nullable = false, length = 100)
    private String motivo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(length = 20)
    private String estado = "PENDIENTE"; // PENDIENTE, RESUELTO, RECHAZADO

    @Column(length = 50)
    private String revisadoPor;

    private LocalDateTime fechaReporte = LocalDateTime.now();
    private LocalDateTime fechaRevision;

    public Reporte() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getContenidoTipo() { return contenidoTipo; }
    public void setContenidoTipo(String contenidoTipo) { this.contenidoTipo = contenidoTipo; }
    public Long getContenidoId() { return contenidoId; }
    public void setContenidoId(Long contenidoId) { this.contenidoId = contenidoId; }
    public String getReportadoPor() { return reportadoPor; }
    public void setReportadoPor(String reportadoPor) { this.reportadoPor = reportadoPor; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getRevisadoPor() { return revisadoPor; }
    public void setRevisadoPor(String revisadoPor) { this.revisadoPor = revisadoPor; }
    public LocalDateTime getFechaReporte() { return fechaReporte; }
    public void setFechaReporte(LocalDateTime fechaReporte) { this.fechaReporte = fechaReporte; }
    public LocalDateTime getFechaRevision() { return fechaRevision; }
    public void setFechaRevision(LocalDateTime fechaRevision) { this.fechaRevision = fechaRevision; }
}