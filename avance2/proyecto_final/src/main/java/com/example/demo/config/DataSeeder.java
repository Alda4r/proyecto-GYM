package com.example.demo.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.demo.model.Ejercicio;
import com.example.demo.model.Rutina;
import com.example.demo.model.Usuario;
import com.example.demo.repository.RutinaRepository;
import com.example.demo.repository.UsuarioRepository;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final RutinaRepository rutinaRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UsuarioRepository usuarioRepository, RutinaRepository rutinaRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rutinaRepository = rutinaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedAdmin();
        seedRoutines();
    }

    private void seedAdmin() {
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

    private void seedRoutines() {
        if (rutinaRepository.count() > 0) return;

        // ========== OBJETIVO: GANAR PESO ==========
        crearRutina("Rutina Pecho - Volumen", "Intermedio", "50 min", "Torso", "Ganar Peso", null, List.of(
            new Ejercicio("Press de banca con barra", 4, 10, "60 kg", null),
            new Ejercicio("Press inclinado con mancuernas", 4, 10, "25 kg", null),
            new Ejercicio("Aperturas con mancuerna", 3, 12, "15 kg", null),
            new Ejercicio("Fondos en paralelas", 3, 10, "peso corporal", null),
            new Ejercicio("Cruce de poleas", 3, 15, "10 kg", null)
        ));

        crearRutina("Rutina Espalda - Volumen", "Intermedio", "45 min", "Torso", "Ganar Peso", null, List.of(
            new Ejercicio("Dominadas", 4, 8, "peso corporal", null),
            new Ejercicio("Remo con barra", 4, 10, "50 kg", null),
            new Ejercicio("Jalón al pecho", 4, 12, "40 kg", null),
            new Ejercicio("Remo en máquina", 3, 12, "35 kg", null),
            new Ejercicio("Curl con barra", 3, 12, "20 kg", null)
        ));

        crearRutina("Rutina Pierna - Potencia", "Intermedio", "50 min", "Tren Inferior", "Ganar Peso", null, List.of(
            new Ejercicio("Sentadilla con barra", 4, 10, "80 kg", null),
            new Ejercicio("Prensa de piernas", 4, 12, "120 kg", null),
            new Ejercicio("Curl de femoral", 3, 12, "30 kg", null),
            new Ejercicio("Extensión de cuádriceps", 3, 12, "40 kg", null),
            new Ejercicio("Elevación de talones", 4, 15, "50 kg", null)
        ));

        // ========== OBJETIVO: PERDER PESO ==========
        crearRutina("Rutina Pecho - Quema Grasa", "Principiante", "40 min", "Torso", "Perder Peso", null, List.of(
            new Ejercicio("Press de banca con mancuernas", 3, 12, "15 kg", null),
            new Ejercicio("Flexiones", 3, 15, "peso corporal", null),
            new Ejercicio("Aperturas en polea", 3, 15, "7 kg", null),
            new Ejercicio("Fondos en banco", 3, 12, "peso corporal", null)
        ));

        crearRutina("Rutina Espalda - Quema Grasa", "Principiante", "40 min", "Torso", "Perder Peso", null, List.of(
            new Ejercicio("Remo con mancuerna", 3, 12, "15 kg", null),
            new Ejercicio("Jalón al pecho", 3, 12, "25 kg", null),
            new Ejercicio("Peso muerto rumano", 3, 12, "40 kg", null),
            new Ejercicio("Curl de bíceps", 3, 12, "10 kg", null)
        ));

        crearRutina("Rutina Pierna - Quema Grasa", "Principiante", "45 min", "Tren Inferior", "Perder Peso", null, List.of(
            new Ejercicio("Sentadilla con mancuerna", 3, 15, "15 kg", null),
            new Ejercicio("Zancadas", 3, 12, "10 kg", null),
            new Ejercicio("Elevación de cadera", 3, 15, "20 kg", null),
            new Ejercicio("Saltos de tijera", 3, 20, "peso corporal", null)
        ));

        // ========== OBJETIVO: HACERSE MÁS FUERTE ==========
        crearRutina("Rutina Pecho - Fuerza Máxima", "Avanzado", "50 min", "Torso", "Hacerse más fuerte", null, List.of(
            new Ejercicio("Press de banca pesado", 5, 5, "80 kg", null),
            new Ejercicio("Press inclinado con barra", 4, 6, "60 kg", null),
            new Ejercicio("Press declinado", 4, 6, "70 kg", null),
            new Ejercicio("Flexiones lastradas", 4, 8, "15 kg", null)
        ));

        crearRutina("Rutina Espalda - Fuerza Máxima", "Avanzado", "50 min", "Torso", "Hacerse más fuerte", null, List.of(
            new Ejercicio("Dominadas con peso", 5, 5, "15 kg", null),
            new Ejercicio("Remo con barra pesado", 5, 5, "70 kg", null),
            new Ejercicio("Peso muerto", 5, 5, "120 kg", null),
            new Ejercicio("Remo en T", 4, 6, "50 kg", null)
        ));

        crearRutina("Rutina Pierna - Fuerza Máxima", "Avanzado", "55 min", "Tren Inferior", "Hacerse más fuerte", null, List.of(
            new Ejercicio("Sentadilla pesada", 5, 5, "120 kg", null),
            new Ejercicio("Peso muerto rumano", 4, 6, "80 kg", null),
            new Ejercicio("Prensa de piernas pesada", 4, 8, "200 kg", null),
            new Ejercicio("Sentadilla búlgara", 3, 8, "30 kg", null),
            new Ejercicio("Elevación de talones", 4, 10, "80 kg", null)
        ));

        crearRutina("Rutina Fuerza - Olímpica", "Avanzado", "60 min", "Torso", "Hacerse más fuerte", null, List.of(
            new Ejercicio("Arrancada", 5, 3, "50 kg", null),
            new Ejercicio("Clean and jerk", 5, 3, "60 kg", null),
            new Ejercicio("Sentadilla frontal", 4, 5, "80 kg", null),
            new Ejercicio("Peso muerto", 4, 5, "100 kg", null)
        ));

        System.out.println("10 rutinas con ejercicios creadas exitosamente.");
    }

    private void crearRutina(String nombre, String nivel, String tiempo, String grupoMuscular,
                             String objetivoAsociado, String usuarioEmail, List<Ejercicio> ejercicios) {
        Rutina rutina = new Rutina(nombre, nivel, tiempo, grupoMuscular, objetivoAsociado, null, usuarioEmail);
        for (Ejercicio e : ejercicios) {
            e.setRutina(rutina);
        }
        rutina.setListaEjercicios(ejercicios);
        rutinaRepository.save(rutina);
    }
}
