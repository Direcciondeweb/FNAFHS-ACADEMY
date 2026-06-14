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

    public Arte guardar(Arte arte) {
        return arteRepository.save(arte);
    }

    public void eliminar(Long id) {
        arteRepository.deleteById(id);
    }
}