package com.example.semana07.repository;

import com.example.semana07.entity.Reporte;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Long> {
    Page<Reporte> findByEstadoOrderByFechaReporteAsc(String estado, Pageable pageable);
    long countByEstado(String estado);
}