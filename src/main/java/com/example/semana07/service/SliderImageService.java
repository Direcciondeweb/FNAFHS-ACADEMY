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

    @Autowired
    private HistorialService historialService;

    public List<SliderImage> listarTodas() {
        return sliderImageRepository.findAll();
    }

    public List<SliderImage> listarActivas() {
        return sliderImageRepository.findByActivoTrueOrderByOrdenAsc();
    }

    public Optional<SliderImage> obtenerPorId(Long id) {
        return sliderImageRepository.findById(id);
    }

    public SliderImage guardar(SliderImage sliderImage, String usuario, String rol) {
        SliderImage guardado = sliderImageRepository.save(sliderImage);
        historialService.registrar(usuario, rol, "CREAR", "SliderImage", String.valueOf(guardado.getId()), "Imagen de slider subida");
        return guardado;
    }

    public void eliminar(Long id, String usuario, String rol) {
        sliderImageRepository.deleteById(id);
        historialService.registrar(usuario, rol, "ELIMINAR", "SliderImage", String.valueOf(id), "Imagen de slider eliminada");
    }

    public void toggleActivo(Long id, Boolean activo, String usuario, String rol) {
        SliderImage imagen = sliderImageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Imagen no encontrada"));
        imagen.setActivo(activo);
        sliderImageRepository.save(imagen);
        historialService.registrar(usuario, rol, "ACTUALIZAR", "SliderImage", String.valueOf(id),
                "Estado activo cambiado a " + activo);
    }
}