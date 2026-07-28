package com.example.semana07.service;

import com.example.semana07.entity.Comentario;
import com.example.semana07.repository.ComentarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ComentarioService {

    @Autowired
    private ComentarioRepository comentarioRepository;

    @Autowired
    private HistorialService historialService;

    public List<Comentario> listarPorContenido(String contenidoTipo, Long contenidoId) {
        return comentarioRepository.findByContenidoTipoAndContenidoIdOrderByFechaDesc(contenidoTipo, contenidoId);
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

    public void eliminar(Long id, String usuario, String rol) {
        comentarioRepository.deleteById(id);
        historialService.registrar(usuario, rol, "ELIMINAR", "Comentario", String.valueOf(id), "Comentario eliminado");
    }
}