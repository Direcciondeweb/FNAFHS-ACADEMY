package com.example.semana07.service;

import com.example.semana07.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class AnalyticsService {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private ArteRepository arteRepository;
    @Autowired private VideoRepository videoRepository;
    @Autowired private PersonajeRepository personajeRepository;
    @Autowired private HistorialAccionRepository historialRepository;

    public Map<String, Object> dashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsuarios", usuarioRepository.count());
        stats.put("totalAdmins", usuarioRepository.countByRol("ADMIN"));
        stats.put("totalSubadmins", usuarioRepository.countByRol("SUBADMIN"));
        stats.put("totalUsers", usuarioRepository.countByRol("USER"));
        stats.put("totalArte", arteRepository.count());
        stats.put("totalVideos", videoRepository.count());
        stats.put("totalPersonajes", personajeRepository.count());
        stats.put("totalAcciones", historialRepository.count());
        stats.put("ultimasAcciones", historialRepository.findAllByOrderByFechaDesc().stream().limit(15).toList());
        stats.put("arteOficial", arteRepository.findByTipoAndEstado("arte-oficial", 1).size());
        stats.put("fanarts", arteRepository.findByTipoAndEstado("fanart", 1).size());
        stats.put("descartados", arteRepository.findByTipoAndEstado("descartado", 1).size());
        return stats;
    }
}