package com.example.demo.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!usuarioRepository.existsById("admin@gym.com")) {
            Usuario admin = new Usuario();
            admin.setEmail("admin@gym.com");
            admin.setNombre("Administrador");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRol("ADMIN");
            admin.setMembresia("Activo");
            admin.setObjetivo("Ganar Peso");
            admin.setPesoActual(75.0);
            admin.setAltura(1.75);
            admin.setMetaCalorias(2500);
            admin.setFechaNacimiento(java.time.LocalDate.of(2000, 1, 1));
            usuarioRepository.save(admin);
            System.out.println("Admin creado: admin@gym.com / admin123");
        }
    }
}