package com.example.semana07.repository;

import com.example.semana07.entity.Notificacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    Page<Notificacion> findByUsuarioOrderByFechaDesc(String usuario, Pageable pageable);
    List<Notificacion> findByUsuarioAndLeidaFalseOrderByFechaDesc(String usuario);
    long countByUsuarioAndLeidaFalse(String usuario);
}