package com.example.semana07.service;

import com.example.semana07.entity.Video;
import com.example.semana07.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class VideoService {

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private HistorialService historialService;

    public List<Video> listarTodos() {
        return videoRepository.findAll();
    }

    public Optional<Video> obtenerPorId(Long id) {
        return videoRepository.findById(id);
    }

    public Video guardar(Video video, String usuario, String rol) {
        Video guardado = videoRepository.save(video);
        historialService.registrar(usuario, rol, "CREAR", "Video",
                String.valueOf(guardado.getId()), "Título: " + guardado.getTitulo());
        return guardado;
    }

    public void eliminar(Long id, String usuario, String rol) {
        videoRepository.deleteById(id);
        historialService.registrar(usuario, rol, "ELIMINAR", "Video", String.valueOf(id), "Video eliminado");
    }
}