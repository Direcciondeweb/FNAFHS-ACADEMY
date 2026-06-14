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

    public List<Video> listarTodos() {
        return videoRepository.findAll();
    }

    public Optional<Video> obtenerPorId(Long id) {
        return videoRepository.findById(id);
    }

    public Video guardar(Video video) {
        return videoRepository.save(video);
    }

    public void eliminar(Long id) {
        videoRepository.deleteById(id);
    }
}