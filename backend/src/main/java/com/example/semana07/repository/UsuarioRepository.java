package com.example.semana07.repository;

import com.example.semana07.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findById(Long id);
    List<Usuario> findByFechaRegistroAfter(java.time.LocalDateTime fecha);
    long countByRol(String rol);
}