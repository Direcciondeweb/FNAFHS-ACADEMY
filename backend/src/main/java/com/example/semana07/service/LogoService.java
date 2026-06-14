package com.example.semana07.service;

import com.example.semana07.entity.Logo;
import com.example.semana07.repository.LogoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class LogoService {

    @Autowired
    private LogoRepository logoRepository;

    public List<Logo> listarTodos() {
        return logoRepository.findAll();
    }

    public Optional<Logo> obtenerPorId(Long id) {
        return logoRepository.findById(id);
    }

    public Logo obtenerActivo() {
        Optional<Logo> activo = logoRepository.findByActivoTrue();
        return activo.orElse(null);
    }

    public Logo guardar(Logo logo) {
        return logoRepository.save(logo);
    }

    public void activar(Long id) {
        // Desactivar todos los logos
        List<Logo> todos = logoRepository.findAll();
        for (Logo logo : todos) {
            logo.setActivo(false);
            logoRepository.save(logo);
        }
        
        // Activar el seleccionado
        Logo logo = logoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Logo no encontrado"));
        logo.setActivo(true);
        logoRepository.save(logo);
    }

    public void eliminar(Long id) {
        Logo logo = logoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Logo no encontrado"));
        logoRepository.deleteById(id);
    }
}