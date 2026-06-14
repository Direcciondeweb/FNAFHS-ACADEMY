package com.example.semana07.service;

import com.example.semana07.entity.Usuario;
import com.example.semana07.repository.UsuarioRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExcelExportService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public byte[] exportarUsuariosAExcel() throws IOException {
        List<Usuario> usuarios = usuarioRepository.findAll();
        
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Usuarios FNAFHS Academy");
        
        // Estilos
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        // Encabezados
        String[] headers = {"ID", "Usuario", "Nombre Completo", "Email", "Teléfono", 
                           "Dirección", "Rol", "Estado", "Fecha Registro"};
        
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Datos
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        int rowNum = 1;
        for (Usuario u : usuarios) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(u.getId());
            row.createCell(1).setCellValue(u.getUsername());
            row.createCell(2).setCellValue(u.getNombreCompleto() != null ? u.getNombreCompleto() : "");
            row.createCell(3).setCellValue(u.getEmail() != null ? u.getEmail() : "");
            row.createCell(4).setCellValue(u.getTelefono() != null ? u.getTelefono() : "");
            row.createCell(5).setCellValue(u.getDireccion() != null ? u.getDireccion() : "");
            row.createCell(6).setCellValue(u.getRol());
            row.createCell(7).setCellValue(u.getEstado() == 1 ? "Activo" : "Inactivo");
            row.createCell(8).setCellValue(u.getFechaRegistro() != null ? 
                u.getFechaRegistro().format(formatter) : "");
        }
        
        // Ajustar anchos
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        
        return outputStream.toByteArray();
    }
    
    public byte[] exportarUsuariosNuevosDesde(LocalDateTime desde) throws IOException {
        List<Usuario> usuarios = usuarioRepository.findByFechaRegistroAfter(desde);
        return exportarUsuariosPersonalizados(usuarios, "Usuarios_Nuevos");
    }
    
    private byte[] exportarUsuariosPersonalizados(List<Usuario> usuarios, String sheetName) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(sheetName);
        
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        String[] headers = {"ID", "Usuario", "Nombre Completo", "Email", "Teléfono", "Rol", "Fecha Registro"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        int rowNum = 1;
        for (Usuario u : usuarios) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(u.getId());
            row.createCell(1).setCellValue(u.getUsername());
            row.createCell(2).setCellValue(u.getNombreCompleto() != null ? u.getNombreCompleto() : "");
            row.createCell(3).setCellValue(u.getEmail() != null ? u.getEmail() : "");
            row.createCell(4).setCellValue(u.getTelefono() != null ? u.getTelefono() : "");
            row.createCell(5).setCellValue(u.getRol());
            row.createCell(6).setCellValue(u.getFechaRegistro() != null ? u.getFechaRegistro().format(formatter) : "");
        }
        
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return outputStream.toByteArray();
    }
}