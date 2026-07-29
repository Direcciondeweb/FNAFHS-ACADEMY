package com.example.semana07.service;

import com.example.semana07.entity.HistorialAccion;
import com.example.semana07.repository.HistorialAccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class HistorialService {

    @Autowired
    private HistorialAccionRepository historialRepository;

    public void registrar(String usuario, String rol, String accion, String entidad, String entidadId, String detalle) {
        HistorialAccion h = new HistorialAccion();
        h.setUsuario(usuario);
        h.setRol(rol);
        h.setAccion(accion);
        h.setEntidad(entidad);
        h.setEntidadId(entidadId);
        h.setDetalle(detalle);
        historialRepository.save(h);
    }

    public List<HistorialAccion> listarTodo() {
        return historialRepository.findAllByOrderByFechaDesc();
    }

    public Page<HistorialAccion> listarPaginado(Pageable pageable) {
        return historialRepository.findAllByOrderByFechaDesc(pageable);
    }

    public List<HistorialAccion> listarPorUsuario(String usuario) {
        return historialRepository.findByUsuarioOrderByFechaDesc(usuario);
    }

    public List<HistorialAccion> listarPorEntidad(String entidad) {
        return historialRepository.findByEntidadOrderByFechaDesc(entidad);
    }

    public List<HistorialAccion> listarPorRango(LocalDateTime desde, LocalDateTime hasta) {
        return historialRepository.findByFechaBetweenOrderByFechaDesc(desde, hasta);
    }
}