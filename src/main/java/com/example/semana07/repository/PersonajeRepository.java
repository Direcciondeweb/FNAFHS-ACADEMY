package com.example.semana07.repository;

import com.example.semana07.entity.Personaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PersonajeRepository extends JpaRepository<Personaje, Long> {
    List<Personaje> findByEstado(Integer estado);
    List<Personaje> findByCategoria(String categoria);
}