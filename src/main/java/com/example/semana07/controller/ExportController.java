package com.example.semana07.controller;

import com.example.semana07.service.ExcelExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/exportar")
@CrossOrigin(origins = "*")
public class ExportController {

    @Autowired
    private ExcelExportService excelExportService;

    private ResponseEntity<byte[]> responder(byte[] data, String nombreBase) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        String filename = nombreBase + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";
        headers.setContentDispositionFormData("attachment", filename);
        return ResponseEntity.ok().headers(headers).body(data);
    }

    @GetMapping("/usuarios")
    public ResponseEntity<byte[]> exportarUsuarios() {
        try {
            return responder(excelExportService.exportarUsuariosAExcel(), "fnafhs_usuarios");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/usuarios/nuevos")
    public ResponseEntity<byte[]> exportarUsuariosNuevos(@RequestParam String desde) {
        try {
            LocalDateTime fecha = LocalDateTime.parse(desde);
            return responder(excelExportService.exportarUsuariosNuevosDesde(fecha), "fnafhs_nuevos_usuarios");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/arte")
    public ResponseEntity<byte[]> exportarArte() {
        try {
            return responder(excelExportService.exportarArte(), "fnafhs_arte");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/videos")
    public ResponseEntity<byte[]> exportarVideos() {
        try {
            return responder(excelExportService.exportarVideos(), "fnafhs_videos");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/personajes")
    public ResponseEntity<byte[]> exportarPersonajes() {
        try {
            return responder(excelExportService.exportarPersonajes(), "fnafhs_personajes");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/historial")
    public ResponseEntity<byte[]> exportarHistorial() {
        try {
            return responder(excelExportService.exportarHistorial(), "fnafhs_historial");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}