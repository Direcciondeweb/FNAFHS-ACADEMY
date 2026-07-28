package com.example.semana07.repository;

import com.example.semana07.entity.HistorialAccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HistorialAccionRepository extends JpaRepository<HistorialAccion, Long> {
    List<HistorialAccion> findAllByOrderByFechaDesc();
    List<HistorialAccion> findByUsuarioOrderByFechaDesc(String usuario);
    List<HistorialAccion> findByEntidadOrderByFechaDesc(String entidad);
    List<HistorialAccion> findByFechaBetweenOrderByFechaDesc(LocalDateTime desde, LocalDateTime hasta);
}