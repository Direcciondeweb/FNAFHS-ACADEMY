package com.example.semana07.service;

import com.example.semana07.entity.InfoSitio;
import com.example.semana07.repository.InfoSitioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class InfoSitioService {

    @Autowired
    private InfoSitioRepository infoSitioRepository;

    /** Siempre hay un único registro (id=1). Si no existe, se crea vacío. */
    public InfoSitio obtener() {
        return infoSitioRepository.findById(1L).orElseGet(() -> {
            InfoSitio nuevo = new InfoSitio();
            nuevo.setVision("");
            nuevo.setMision("");
            return infoSitioRepository.save(nuevo);
        });
    }

    public InfoSitio actualizar(String vision, String mision) {
        InfoSitio info = obtener();
        info.setVision(vision);
        info.setMision(mision);
        info.setFechaActualizacion(LocalDateTime.now());
        return infoSitioRepository.save(info);
    }
}