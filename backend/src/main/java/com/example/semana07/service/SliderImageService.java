package com.example.semana07.service;

import com.example.semana07.entity.SliderImage;
import com.example.semana07.repository.SliderImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class SliderImageService {

    @Autowired
    private SliderImageRepository sliderImageRepository;

    public List<SliderImage> listarTodas() {
        return sliderImageRepository.findAll();
    }

    public List<SliderImage> listarActivas() {
        return sliderImageRepository.findByActivoTrueOrderByOrdenAsc();
    }

    public Optional<SliderImage> obtenerPorId(Long id) {
        return sliderImageRepository.findById(id);
    }

    public SliderImage guardar(SliderImage sliderImage) {
        return sliderImageRepository.save(sliderImage);
    }

    public void eliminar(Long id) {
        sliderImageRepository.deleteById(id);
    }

    public void toggleActivo(Long id, Boolean activo) {
        SliderImage imagen = sliderImageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Imagen no encontrada"));
        imagen.setActivo(activo);
        sliderImageRepository.save(imagen);
    }
}