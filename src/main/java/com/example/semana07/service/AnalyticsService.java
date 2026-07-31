package com.example.semana07.service;

import com.example.semana07.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AnalyticsService {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private ArteRepository arteRepository;
    @Autowired private VideoRepository videoRepository;
    @Autowired private PersonajeRepository personajeRepository;
    @Autowired private HistorialAccionRepository historialRepository;
    @Autowired private LikeContenidoRepository likeRepository;
    @Autowired private ComentarioRepository comentarioRepository;
    @Autowired private ReporteRepository reporteRepository;

    public Map<String, Object> dashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsuarios", usuarioRepository.count());
        stats.put("totalAdmins", usuarioRepository.countByRol("ADMIN"));
        stats.put("totalSubadmins", usuarioRepository.countByRol("SUBADMIN"));
        stats.put("totalUsers", usuarioRepository.countByRol("USER"));
        stats.put("totalArte", arteRepository.count());
        stats.put("totalVideos", videoRepository.count());
        stats.put("totalPersonajes", personajeRepository.count());
        stats.put("totalComentarios", comentarioRepository.count());
        stats.put("totalReportesPendientes", reporteRepository.countByEstado("PENDIENTE"));
        stats.put("totalAcciones", historialRepository.count());
        stats.put("ultimasAcciones", historialRepository.findAllByOrderByFechaDesc().stream().limit(10).toList());

        stats.put("arteOficial", arteRepository.findByTipoAndEstado("arte-oficial", 1).size());
        stats.put("fanarts", arteRepository.findByTipoAndEstado("fanart", 1).size());
        stats.put("descartados", arteRepository.findByTipoAndEstado("descartado", 1).size());
        stats.put("comics", arteRepository.findByTipo("comic").size());

        // Datos para gráfico de barras: distribución de contenido por categoría
        Map<String, Long> contenidoPorTipo = new LinkedHashMap<>();
        contenidoPorTipo.put("Arte Oficial", (long) arteRepository.findByTipoAndEstado("arte-oficial", 1).size());
        contenidoPorTipo.put("Fanarts", (long) arteRepository.findByTipoAndEstado("fanart", 1).size());
        contenidoPorTipo.put("Comics", (long) arteRepository.findByTipo("comic").size());
        contenidoPorTipo.put("Videos", videoRepository.count());
        contenidoPorTipo.put("Descartados", (long) arteRepository.findByTipoAndEstado("descartado", 1).size());
        stats.put("contenidoPorTipo", contenidoPorTipo);

        // Distribución de usuarios por rol (para gráfico de dona)
        Map<String, Long> usuariosPorRol = new LinkedHashMap<>();
        usuariosPorRol.put("Admin", usuarioRepository.countByRol("ADMIN"));
        usuariosPorRol.put("Subadmin", usuarioRepository.countByRol("SUBADMIN"));
        usuariosPorRol.put("Usuario", usuarioRepository.countByRol("USER"));
        stats.put("usuariosPorRol", usuariosPorRol);

        return stats;
    }

    /** Contenido más popular por likes, combinando arte y videos. */
    public List<Map<String, Object>> topContenidoPorLikes(int limite) {
        List<Map<String, Object>> resultado = new ArrayList<>();

        arteRepository.findAll().forEach(a -> {
            long likes = likeRepository.countByContenidoTipoAndContenidoId("ARTE", a.getId());
            long comentarios = comentarioRepository.countByContenidoTipoAndContenidoId("ARTE", a.getId());
            Map<String, Object> item = new HashMap<>();
            item.put("tipo", "ARTE");
            item.put("id", a.getId());
            item.put("titulo", a.getTitulo());
            item.put("imagenUrl", a.getImagenUrl());
            item.put("likes", likes);
            item.put("comentarios", comentarios);
            resultado.add(item);
        });

        videoRepository.findAll().forEach(v -> {
            long likes = likeRepository.countByContenidoTipoAndContenidoId("VIDEO", v.getId());
            long comentarios = comentarioRepository.countByContenidoTipoAndContenidoId("VIDEO", v.getId());
            Map<String, Object> item = new HashMap<>();
            item.put("tipo", "VIDEO");
            item.put("id", v.getId());
            item.put("titulo", v.getTitulo());
            item.put("imagenUrl", v.getVideoUrl());
            item.put("likes", likes);
            item.put("comentarios", comentarios);
            resultado.add(item);
        });

        resultado.sort((a, b) -> Long.compare((long) b.get("likes"), (long) a.get("likes")));
        return resultado.stream().limit(limite).toList();
    }
}