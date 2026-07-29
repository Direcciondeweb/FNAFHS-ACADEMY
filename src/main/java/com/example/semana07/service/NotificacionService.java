package com.example.semana07.service;

import com.example.semana07.entity.Notificacion;
import com.example.semana07.exception.ResourceNotFoundException;
import com.example.semana07.repository.NotificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificacionService {

    @Autowired
    private NotificacionRepository notificacionRepository;

    public Notificacion crear(String usuario, String titulo, String mensaje, String tipo, String link) {
        Notificacion n = new Notificacion();
        n.setUsuario(usuario);
        n.setTitulo(titulo);
        n.setMensaje(mensaje);
        n.setTipo(tipo);
        n.setLink(link);
        n.setLeida(false);
        return notificacionRepository.save(n);
    }

    public Page<Notificacion> listarPorUsuario(String usuario, Pageable pageable) {
        return notificacionRepository.findByUsuarioOrderByFechaDesc(usuario, pageable);
    }

    public List<Notificacion> listarNoLeidas(String usuario) {
        return notificacionRepository.findByUsuarioAndLeidaFalseOrderByFechaDesc(usuario);
    }

    public long contarNoLeidas(String usuario) {
        return notificacionRepository.countByUsuarioAndLeidaFalse(usuario);
    }

    public void marcarLeida(Long id) {
        Notificacion n = notificacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificación no encontrada"));
        n.setLeida(true);
        notificacionRepository.save(n);
    }

    public void marcarTodasLeidas(String usuario) {
        List<Notificacion> noLeidas = notificacionRepository.findByUsuarioAndLeidaFalseOrderByFechaDesc(usuario);
        noLeidas.forEach(n -> n.setLeida(true));
        notificacionRepository.saveAll(noLeidas);
    }
}