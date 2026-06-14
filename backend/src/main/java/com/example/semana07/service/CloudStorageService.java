package com.example.semana07.service;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.UUID;

@Service
public class CloudStorageService {

    @Value("${supabase.url:}")
    private String supabaseUrl;
    
    @Value("${supabase.key:}")
    private String supabaseKey;
    
    @Value("${supabase.storage.bucket:fnafhs-images}")
    private String bucketName;
    
    @Value("${file.upload-type:local}")
    private String uploadType;
    
    private final OkHttpClient client = new OkHttpClient();
    
    public String uploadImage(MultipartFile file, String folder) throws IOException {
        if ("local".equals(uploadType) || supabaseUrl.isEmpty()) {
            return null; // Se maneja localmente
        }
        
        String fileName = folder + "/" + UUID.randomUUID().toString() + getExtension(file.getOriginalFilename());
        
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName,
                        RequestBody.create(file.getBytes(), MediaType.parse(file.getContentType())))
                .build();
        
        Request request = new Request.Builder()
                .url(supabaseUrl + "/storage/v1/object/" + bucketName + "/" + fileName)
                .header("Authorization", "Bearer " + supabaseKey)
                .header("apikey", supabaseKey)
                .put(requestBody)
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Error uploading to Supabase: " + response);
            }
            return supabaseUrl + "/storage/v1/object/public/" + bucketName + "/" + fileName;
        }
    }
    
    public void deleteFile(String fileUrl) throws IOException {
        if ("local".equals(uploadType) || supabaseUrl.isEmpty() || fileUrl == null) {
            return;
        }
        
        String path = fileUrl.substring(fileUrl.indexOf(bucketName) + bucketName.length() + 1);
        
        Request request = new Request.Builder()
                .url(supabaseUrl + "/storage/v1/object/" + bucketName + "/" + path)
                .header("Authorization", "Bearer " + supabaseKey)
                .header("apikey", supabaseKey)
                .delete()
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Error deleting from Supabase");
            }
        }
    }
    
    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf("."));
    }
}