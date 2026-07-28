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

    @Autowired
    private HistorialService historialService;

    public List<Logo> listarTodos() {
        return logoRepository.findAll();
    }

    public Optional<Logo> obtenerPorId(Long id) {
        return logoRepository.findById(id);
    }

    public Logo obtenerActivo() {
        return logoRepository.findByActivoTrue().orElse(null);
    }

    public Logo guardar(Logo logo, String usuario, String rol) {
        Logo guardado = logoRepository.save(logo);
        historialService.registrar(usuario, rol, "CREAR", "Logo", String.valueOf(guardado.getId()), "Logo subido");
        return guardado;
    }

    public void activar(Long id, String usuario, String rol) {
        List<Logo> todos = logoRepository.findAll();
        for (Logo logo : todos) {
            logo.setActivo(false);
            logoRepository.save(logo);
        }
        Logo logo = logoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Logo no encontrado"));
        logo.setActivo(true);
        logoRepository.save(logo);
        historialService.registrar(usuario, rol, "ACTIVAR", "Logo", String.valueOf(id), "Logo activado");
    }

    public void eliminar(Long id, String usuario, String rol) {
        logoRepository.deleteById(id);
        historialService.registrar(usuario, rol, "ELIMINAR", "Logo", String.valueOf(id), "Logo eliminado");
    }
}