package com.example.semana07.service;

import com.example.semana07.entity.LikeContenido;
import com.example.semana07.repository.LikeContenidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class LikeContenidoService {

    @Autowired
    private LikeContenidoRepository likeRepository;

    @Autowired
    private HistorialService historialService;

    @Transactional
    public boolean toggle(String contenidoTipo, Long contenidoId, String usuario, String rol) {
        boolean yaLeGusta = likeRepository.existsByContenidoTipoAndContenidoIdAndUsuario(contenidoTipo, contenidoId, usuario);

        if (yaLeGusta) {
            likeRepository.deleteByContenidoTipoAndContenidoIdAndUsuario(contenidoTipo, contenidoId, usuario);
            return false;
        } else {
            LikeContenido like = new LikeContenido();
            like.setContenidoTipo(contenidoTipo);
            like.setContenidoId(contenidoId);
            like.setUsuario(usuario);
            like.setFecha(LocalDateTime.now());
            likeRepository.save(like);
            historialService.registrar(usuario, rol, "LIKE", contenidoTipo, String.valueOf(contenidoId), "Le gusta este contenido");
            return true;
        }
    }

    public long contarLikes(String contenidoTipo, Long contenidoId) {
        return likeRepository.countByContenidoTipoAndContenidoId(contenidoTipo, contenidoId);
    }

    public boolean usuarioLeGusta(String contenidoTipo, Long contenidoId, String usuario) {
        return likeRepository.existsByContenidoTipoAndContenidoIdAndUsuario(contenidoTipo, contenidoId, usuario);
    }
}