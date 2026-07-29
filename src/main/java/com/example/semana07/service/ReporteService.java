package com.example.semana07.service;

import com.example.semana07.entity.Reporte;
import com.example.semana07.exception.ResourceNotFoundException;
import com.example.semana07.repository.ReporteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReporteService {

    @Autowired private ReporteRepository reporteRepository;
    @Autowired private NotificacionService notificacionService;
    @Autowired private HistorialService historialService;

    public Reporte crear(String contenidoTipo, Long contenidoId, String reportadoPor, String motivo, String descripcion) {
        Reporte reporte = new Reporte();
        reporte.setContenidoTipo(contenidoTipo);
        reporte.setContenidoId(contenidoId);
        reporte.setReportadoPor(reportadoPor);
        reporte.setMotivo(motivo);
        reporte.setDescripcion(descripcion);
        reporte.setEstado("PENDIENTE");
        Reporte guardado = reporteRepository.save(reporte);

        historialService.registrar(reportadoPor, "USER", "CREAR", "Reporte",
                String.valueOf(guardado.getId()), "Reporte de " + contenidoTipo + " #" + contenidoId);
        return guardado;
    }

    public Page<Reporte> listarPendientes(Pageable pageable) {
        return reporteRepository.findByEstadoOrderByFechaReporteAsc("PENDIENTE", pageable);
    }

    public Page<Reporte> listarTodos(Pageable pageable) {
        return reporteRepository.findAll(pageable);
    }

    public long contarPendientes() {
        return reporteRepository.countByEstado("PENDIENTE");
    }

    public Reporte resolver(Long id, String revisadoPor, String rolRevisor) {
        Reporte reporte = obtenerOLanzar(id);
        reporte.setEstado("RESUELTO");
        reporte.setRevisadoPor(revisadoPor);
        reporte.setFechaRevision(LocalDateTime.now());
        Reporte actualizado = reporteRepository.save(reporte);

        notificacionService.crear(reporte.getReportadoPor(), "Reporte resuelto",
                "Tu reporte sobre " + reporte.getContenidoTipo() + " #" + reporte.getContenidoId() + " fue revisado y resuelto.",
                "REPORTE_RESUELTO", null);

        historialService.registrar(revisadoPor, rolRevisor, "RESOLVER", "Reporte", String.valueOf(id), "Reporte resuelto");
        return actualizado;
    }

    public Reporte rechazar(Long id, String revisadoPor, String rolRevisor) {
        Reporte reporte = obtenerOLanzar(id);
        reporte.setEstado("RECHAZADO");
        reporte.setRevisadoPor(revisadoPor);
        reporte.setFechaRevision(LocalDateTime.now());
        Reporte actualizado = reporteRepository.save(reporte);

        notificacionService.crear(reporte.getReportadoPor(), "Reporte rechazado",
                "Tu reporte sobre " + reporte.getContenidoTipo() + " #" + reporte.getContenidoId() + " fue revisado y rechazado.",
                "REPORTE_RESUELTO", null);

        historialService.registrar(revisadoPor, rolRevisor, "RECHAZAR", "Reporte", String.valueOf(id), "Reporte rechazado");
        return actualizado;
    }

    private Reporte obtenerOLanzar(Long id) {
        return reporteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reporte no encontrado"));
    }
}