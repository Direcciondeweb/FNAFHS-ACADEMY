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

    @GetMapping("/usuarios")
    public ResponseEntity<byte[]> exportarTodosLosUsuarios() {
        try {
            byte[] excelData = excelExportService.exportarUsuariosAExcel();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            String filename = "fnafhs_usuarios_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";
            headers.setContentDispositionFormData("attachment", filename);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelData);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/usuarios/nuevos")
    public ResponseEntity<byte[]> exportarUsuariosNuevos(@RequestParam String desde) {
        try {
            LocalDateTime fecha = LocalDateTime.parse(desde);
            byte[] excelData = excelExportService.exportarUsuariosNuevosDesde(fecha);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            String filename = "fnafhs_nuevos_usuarios_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";
            headers.setContentDispositionFormData("attachment", filename);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelData);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}