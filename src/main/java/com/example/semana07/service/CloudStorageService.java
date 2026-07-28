package com.example.semana07.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;

@Service
public class CloudStorageService {

    @Autowired
    private Cloudinary cloudinary;

    public String uploadFile(MultipartFile file, String folder) throws IOException {
        Map<String, Object> options = ObjectUtils.asMap(
                "folder", "fnafhs-academy/" + folder,
                "resource_type", "auto",
                "use_filename", true,
                "unique_filename", true
        );

        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
        return uploadResult.get("secure_url").toString();
    }

    /**
     * Elimina un archivo de Cloudinary a partir de su public_id.
     * Cloudinary necesita el public_id (no la URL completa) para borrar.
     */
    public void deleteFile(String publicId) throws IOException {
        if (publicId == null || publicId.isBlank()) return;
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }

    /**
     * Extrae el public_id de una URL de Cloudinary para poder eliminarlo después.
     * Ejemplo de URL: https://res.cloudinary.com/tu-cloud/image/upload/v123456/fnafhs-academy/arte/abc123.jpg
     * public_id resultante: fnafhs-academy/arte/abc123
     */
    public String extraerPublicId(String url) {
        if (url == null) return null;
        try {
            String[] partes = url.split("/upload/");
            if (partes.length < 2) return null;
            String resto = partes[1];
            // Quita el prefijo de versión "v123456/"
            resto = resto.replaceFirst("^v\\d+/", "");
            // Quita la extensión del archivo
            int lastDot = resto.lastIndexOf(".");
            return lastDot > 0 ? resto.substring(0, lastDot) : resto;
        } catch (Exception e) {
            return null;
        }
    }
}