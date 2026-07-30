package com.example.semana07.repository;

import com.example.semana07.entity.InfoSitio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InfoSitioRepository extends JpaRepository<InfoSitio, Long> {
}