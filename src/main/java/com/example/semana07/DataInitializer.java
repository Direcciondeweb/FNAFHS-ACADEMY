package com.example.semana07;

import com.example.semana07.entity.Usuario;
import com.example.semana07.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (usuarioRepository.count() == 0) {
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRol("ADMIN");
            admin.setNombreCompleto("Administrador");
            admin.setEmail("dianix970@gmail.com");
            admin.setEstado(1);
            usuarioRepository.save(admin);

            Usuario subadmin = new Usuario();
            subadmin.setUsername("subadmin");
            subadmin.setPassword(passwordEncoder.encode("subadmin123"));
            subadmin.setRol("SUBADMIN");
            subadmin.setNombreCompleto("Sub Administrador");
            subadmin.setEmail("subadmin@fnafhsacademy.com");
            subadmin.setEstado(1);
            usuarioRepository.save(subadmin);

            System.out.println("✅ Usuario ADMIN creado por defecto: admin / admin123");
            System.out.println("✅ Usuario SUBADMIN creado por defecto: subadmin / subadmin123");
        }
    }
}