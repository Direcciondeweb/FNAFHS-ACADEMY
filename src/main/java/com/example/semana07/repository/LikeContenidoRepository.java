package com.example.semana07.repository;

import com.example.semana07.entity.LikeContenido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface LikeContenidoRepository extends JpaRepository<LikeContenido, Long> {
    Optional<LikeContenido> findByContenidoTipoAndContenidoIdAndUsuario(String contenidoTipo, Long contenidoId, String usuario);
    long countByContenidoTipoAndContenidoId(String contenidoTipo, Long contenidoId);
    boolean existsByContenidoTipoAndContenidoIdAndUsuario(String contenidoTipo, Long contenidoId, String usuario);
    void deleteByContenidoTipoAndContenidoIdAndUsuario(String contenidoTipo, Long contenidoId, String usuario);
}