package com.example.semana07;

import com.example.semana07.entity.Usuario;
import com.example.semana07.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public void run(String... args) throws Exception {
        // Crear usuario admin si no existe
        if (usuarioRepository.count() == 0) {
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setPassword("admin123");
            admin.setRol("ADMIN");
            admin.setNombreCompleto("Administrador");
            admin.setEmail("admin@gmail.com");
            admin.setEstado(1);
            usuarioRepository.save(admin);

            Usuario subadmin = new Usuario();
            subadmin.setUsername("subadmin");
            subadmin.setPassword("subadmin123");
            subadmin.setRol("SUBADMIN");
            subadmin.setNombreCompleto("Sub Administrador");
            subadmin.setEmail("subadmin@gmail.com");
            subadmin.setEstado(1);
            usuarioRepository.save(subadmin);

            Usuario user = new Usuario();
            user.setUsername("user");
            user.setPassword("user123");
            user.setRol("USER");
            user.setNombreCompleto("Usuario Normal");
            user.setEmail("usuario@gmail.com");
            user.setEstado(1);
            usuarioRepository.save(user);

            System.out.println("✅ Usuarios por defecto creados");
        }
    }
}