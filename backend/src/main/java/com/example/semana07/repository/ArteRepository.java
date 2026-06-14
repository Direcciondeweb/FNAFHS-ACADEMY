package com.example.semana07.repository;

import com.example.semana07.entity.Arte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ArteRepository extends JpaRepository<Arte, Long> {
    List<Arte> findByTipoAndEstado(String tipo, Integer estado);
    List<Arte> findByComicIdOrderByIdAsc(String comicId);
    List<Arte> findByTipo(String tipo);
}