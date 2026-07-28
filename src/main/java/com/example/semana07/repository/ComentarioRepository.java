package com.example.semana07.repository;

import com.example.semana07.entity.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    List<Comentario> findByContenidoTipoAndContenidoIdOrderByFechaDesc(String contenidoTipo, Long contenidoId);
    long countByContenidoTipoAndContenidoId(String contenidoTipo, Long contenidoId);
}