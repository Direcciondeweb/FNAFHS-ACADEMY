package com.example.semana07.repository;

import com.example.semana07.entity.Logo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface LogoRepository extends JpaRepository<Logo, Long> {
    Optional<Logo> findByActivoTrue();
}