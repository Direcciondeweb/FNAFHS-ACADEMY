package com.example.semana07.service;

import com.example.semana07.entity.*;
import com.example.semana07.repository.*;
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

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private ArteRepository arteRepository;
    @Autowired private VideoRepository videoRepository;
    @Autowired private PersonajeRepository personajeRepository;
    @Autowired private HistorialAccionRepository historialRepository;

    private CellStyle headerStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    public byte[] exportarUsuariosAExcel() throws IOException {
        List<Usuario> usuarios = usuarioRepository.findAll();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Usuarios");
        CellStyle headerStyle = headerStyle(workbook);

        String[] headers = {"ID", "Usuario", "Nombre Completo", "Email", "Teléfono", "Dirección", "Rol", "Estado", "Fecha Registro"};
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
            row.createCell(5).setCellValue(u.getDireccion() != null ? u.getDireccion() : "");
            row.createCell(6).setCellValue(u.getRol());
            row.createCell(7).setCellValue(u.getEstado() == 1 ? "Activo" : "Inactivo");
            row.createCell(8).setCellValue(u.getFechaRegistro() != null ? u.getFechaRegistro().format(formatter) : "");
        }
        for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);
        return toBytes(workbook);
    }

    public byte[] exportarUsuariosNuevosDesde(LocalDateTime desde) throws IOException {
        List<Usuario> usuarios = usuarioRepository.findByFechaRegistroAfter(desde);
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Usuarios Nuevos");
        CellStyle headerStyle = headerStyle(workbook);

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
        for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);
        return toBytes(workbook);
    }

    public byte[] exportarArte() throws IOException {
        List<Arte> lista = arteRepository.findAll();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Arte");
        CellStyle headerStyle = headerStyle(workbook);

        String[] headers = {"ID", "Título", "Tipo", "Comic ID", "Total Páginas", "Estado", "Fecha Registro"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        int rowNum = 1;
        for (Arte a : lista) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(a.getId());
            row.createCell(1).setCellValue(a.getTitulo() != null ? a.getTitulo() : "");
            row.createCell(2).setCellValue(a.getTipo() != null ? a.getTipo() : "");
            row.createCell(3).setCellValue(a.getComicId() != null ? a.getComicId() : "");
            row.createCell(4).setCellValue(a.getTotalPaginas() != null ? a.getTotalPaginas() : 0);
            row.createCell(5).setCellValue(a.getEstado() == 1 ? "Activo" : "Inactivo");
            row.createCell(6).setCellValue(a.getFechaRegistro() != null ? a.getFechaRegistro().format(formatter) : "");
        }
        for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);
        return toBytes(workbook);
    }

    public byte[] exportarVideos() throws IOException {
        List<Video> lista = videoRepository.findAll();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Videos");
        CellStyle headerStyle = headerStyle(workbook);

        String[] headers = {"ID", "Título", "Estado", "Fecha Registro"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        int rowNum = 1;
        for (Video v : lista) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(v.getId());
            row.createCell(1).setCellValue(v.getTitulo());
            row.createCell(2).setCellValue(v.getEstado() == 1 ? "Activo" : "Inactivo");
            row.createCell(3).setCellValue(v.getFechaRegistro() != null ? v.getFechaRegistro().format(formatter) : "");
        }
        for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);
        return toBytes(workbook);
    }

    public byte[] exportarPersonajes() throws IOException {
        List<Personaje> lista = personajeRepository.findAll();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Personajes");
        CellStyle headerStyle = headerStyle(workbook);

        String[] headers = {"ID", "Nombre", "Categoría", "Estado", "Fecha Registro"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        int rowNum = 1;
        for (Personaje p : lista) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(p.getId());
            row.createCell(1).setCellValue(p.getNombre());
            row.createCell(2).setCellValue(p.getCategoria() != null ? p.getCategoria() : "");
            row.createCell(3).setCellValue(p.getEstado() == 1 ? "Activo" : "Inactivo");
            row.createCell(4).setCellValue(p.getFechaRegistro() != null ? p.getFechaRegistro().format(formatter) : "");
        }
        for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);
        return toBytes(workbook);
    }

    public byte[] exportarHistorial() throws IOException {
        List<HistorialAccion> lista = historialRepository.findAllByOrderByFechaDesc();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Historial");
        CellStyle headerStyle = headerStyle(workbook);

        String[] headers = {"ID", "Usuario", "Rol", "Acción", "Entidad", "Entidad ID", "Detalle", "Fecha"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        int rowNum = 1;
        for (HistorialAccion h : lista) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(h.getId());
            row.createCell(1).setCellValue(h.getUsuario() != null ? h.getUsuario() : "");
            row.createCell(2).setCellValue(h.getRol() != null ? h.getRol() : "");
            row.createCell(3).setCellValue(h.getAccion() != null ? h.getAccion() : "");
            row.createCell(4).setCellValue(h.getEntidad() != null ? h.getEntidad() : "");
            row.createCell(5).setCellValue(h.getEntidadId() != null ? h.getEntidadId() : "");
            row.createCell(6).setCellValue(h.getDetalle() != null ? h.getDetalle() : "");
            row.createCell(7).setCellValue(h.getFecha() != null ? h.getFecha().format(formatter) : "");
        }
        for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);
        return toBytes(workbook);
    }

    private byte[] toBytes(Workbook workbook) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return outputStream.toByteArray();
    }
}