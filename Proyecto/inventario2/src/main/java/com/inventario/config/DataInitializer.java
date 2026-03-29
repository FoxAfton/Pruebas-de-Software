package com.inventario.config;

import com.inventario.model.Usuario;
import com.inventario.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (!usuarioRepository.existsByEmail("admin@inventario.com")) {
                Usuario admin = Usuario.builder()
                        .nombre("Administrador")
                        .email("admin@inventario.com")
                        .password(passwordEncoder.encode("Admin123!"))
                        .rol(Usuario.Rol.ADMIN)
                        .build();
                usuarioRepository.save(admin);
                System.out.println("Usuario admin creado: admin@inventario.com / Admin123!");
            }
        };
    }
}
