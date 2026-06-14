package com.example.semana07.repository;

import com.example.semana07.entity.SliderImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SliderImageRepository extends JpaRepository<SliderImage, Long> {
    List<SliderImage> findByActivoTrueOrderByOrdenAsc();
}