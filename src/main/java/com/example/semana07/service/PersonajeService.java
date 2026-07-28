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

    @Autowired
    private HistorialService historialService;

    public List<Personaje> listarTodos() {
        return personajeRepository.findAll();
    }

    public List<Personaje> listarActivos() {
        return personajeRepository.findByEstado(1);
    }

    public Optional<Personaje> obtenerPorId(Long id) {
        return personajeRepository.findById(id);
    }

    public Personaje guardar(Personaje personaje, String usuario, String rol) {
        boolean esNuevo = personaje.getId() == null;
        Personaje guardado = personajeRepository.save(personaje);
        historialService.registrar(usuario, rol, esNuevo ? "CREAR" : "ACTUALIZAR", "Personaje",
                String.valueOf(guardado.getId()), "Nombre: " + guardado.getNombre());
        return guardado;
    }

    public void eliminar(Long id, String usuario, String rol) {
        personajeRepository.deleteById(id);
        historialService.registrar(usuario, rol, "ELIMINAR", "Personaje", String.valueOf(id), "Personaje eliminado");
    }

    public void actualizarEstado(Long id, Integer estado) {
        Personaje personaje = personajeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Personaje no encontrado"));
        personaje.setEstado(estado);
        personajeRepository.save(personaje);
    }
}