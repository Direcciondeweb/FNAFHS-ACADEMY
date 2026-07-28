package com.example.semana07.service;

import com.example.semana07.entity.Arte;
import com.example.semana07.repository.ArteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ArteService {

    @Autowired
    private ArteRepository arteRepository;

    @Autowired
    private HistorialService historialService;

    public List<Arte> listarTodos() {
        return arteRepository.findAll();
    }

    public List<Arte> listarPorTipo(String tipo) {
        return arteRepository.findByTipoAndEstado(tipo, 1);
    }

    public List<Arte> listarComicPorId(String comicId) {
        return arteRepository.findByComicIdOrderByIdAsc(comicId);
    }

    public Optional<Arte> obtenerPorId(Long id) {
        return arteRepository.findById(id);
    }

    public Arte guardar(Arte arte, String usuario, String rol) {
        Arte guardado = arteRepository.save(arte);
        historialService.registrar(usuario, rol, "CREAR", "Arte",
                String.valueOf(guardado.getId()), "Título: " + guardado.getTitulo() + " (" + guardado.getTipo() + ")");
        return guardado;
    }

    public void eliminar(Long id, String usuario, String rol) {
        arteRepository.deleteById(id);
        historialService.registrar(usuario, rol, "ELIMINAR", "Arte", String.valueOf(id), "Arte eliminado");
    }
}