package com.example.semana07.service;

import com.example.semana07.entity.Personaje;
import com.example.semana07.repository.PersonajeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PersonajeService {

    @Autowired
    private PersonajeRepository personajeRepository;

    public List<Personaje> listarTodos() {
        return personajeRepository.findAll();
    }

    public List<Personaje> listarActivos() {
        return personajeRepository.findByEstado(1);
    }

    public Optional<Personaje> obtenerPorId(Long id) {
        return personajeRepository.findById(id);
    }

    public Personaje guardar(Personaje personaje) {
        return personajeRepository.save(personaje);
    }

    public void eliminar(Long id) {
        personajeRepository.deleteById(id);
    }

    public void actualizarEstado(Long id, Integer estado) {
        Personaje personaje = personajeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Personaje no encontrado"));
        personaje.setEstado(estado);
        personajeRepository.save(personaje);
    }
}