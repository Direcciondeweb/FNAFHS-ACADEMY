package com.example.semana07.service;

import com.example.semana07.entity.Comentario;
import com.example.semana07.exception.ResourceNotFoundException;
import com.example.semana07.repository.ComentarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComentarioService {

    @Autowired private ComentarioRepository comentarioRepository;
    @Autowired private HistorialService historialService;

    public List<Comentario> listarPorContenido(String contenidoTipo, Long contenidoId) {
        return comentarioRepository.findByContenidoTipoAndContenidoIdOrderByFijadoDescFechaDesc(contenidoTipo, contenidoId);
    }

    public long contarPorContenido(String contenidoTipo, Long contenidoId) {
        return comentarioRepository.countByContenidoTipoAndContenidoId(contenidoTipo, contenidoId);
    }

    public Comentario crear(String contenidoTipo, Long contenidoId, String usuario, String rol, String texto) {
        Comentario comentario = new Comentario();
        comentario.setContenidoTipo(contenidoTipo);
        comentario.setContenidoId(contenidoId);
        comentario.setUsuario(usuario);
        comentario.setTexto(texto);
        comentario.setAprobado(true);

        Comentario guardado = comentarioRepository.save(comentario);
        historialService.registrar(usuario, rol, "CREAR", "Comentario",
                String.valueOf(guardado.getId()), "Comentario en " + contenidoTipo + " #" + contenidoId);
        return guardado;
    }

    public Page<Comentario> listarTodosAdmin(Pageable pageable) {
        return comentarioRepository.findAllByOrderByFechaDesc(pageable);
    }

    public void fijar(Long id, boolean fijado, String admin, String rol) {
        Comentario c = obtenerOLanzar(id);
        c.setFijado(fijado);
        comentarioRepository.save(c);
        historialService.registrar(admin, rol, fijado ? "FIJAR" : "DESFIJAR", "Comentario",
                String.valueOf(id), "Comentario " + (fijado ? "fijado" : "desfijado"));
    }

    public void censurar(Long id, boolean censurado, String admin, String rol) {
        Comentario c = obtenerOLanzar(id);
        c.setCensurado(censurado);
        comentarioRepository.save(c);
        historialService.registrar(admin, rol, censurado ? "CENSURAR" : "DESCENSURAR", "Comentario",
                String.valueOf(id), "Comentario " + (censurado ? "censurado" : "descensurado"));
    }

    public void eliminar(Long id, String usuario, String rol) {
        comentarioRepository.deleteById(id);
        historialService.registrar(usuario, rol, "ELIMINAR", "Comentario", String.valueOf(id), "Comentario eliminado");
    }

    private Comentario obtenerOLanzar(Long id) {
        return comentarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comentario no encontrado"));
    }
}