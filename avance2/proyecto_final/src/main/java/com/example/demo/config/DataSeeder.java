package com.example.demo.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.demo.model.Ejercicio;
import com.example.demo.model.Producto;
import com.example.demo.model.Rutina;
import com.example.demo.model.Usuario;
import com.example.demo.repository.ProductoRepository;
import com.example.demo.repository.RutinaRepository;
import com.example.demo.repository.UsuarioRepository;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final RutinaRepository rutinaRepository;
    private final ProductoRepository productoRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UsuarioRepository usuarioRepository, RutinaRepository rutinaRepository,
                      ProductoRepository productoRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rutinaRepository = rutinaRepository;
        this.productoRepository = productoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedAdmin();
        seedRoutines();
        seedProductos();
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
        if (rutinaRepository.count() >= 16) return;

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

        crearRutina("Rutina Pecho y Hombros - Masa", "Intermedio", "45 min", "Torso", "Ganar Peso", null, List.of(
            new Ejercicio("Press militar con barra", 4, 10, "35 kg", null),
            new Ejercicio("Press inclinado con barra", 4, 10, "55 kg", null),
            new Ejercicio("Elevaciones laterales", 3, 15, "10 kg", null),
            new Ejercicio("Aperturas en polea alta", 3, 12, "12 kg", null),
            new Ejercicio("Face pull", 3, 15, "15 kg", null)
        ));

        crearRutina("Rutina Espalda y Trapecio - Volumen", "Intermedio", "50 min", "Torso", "Ganar Peso", null, List.of(
            new Ejercicio("Peso muerto con barra", 4, 8, "90 kg", null),
            new Ejercicio("Remo en T", 4, 10, "45 kg", null),
            new Ejercicio("Dominadas con lastre", 4, 8, "10 kg", null),
            new Ejercicio("Encogimientos de trapecio", 4, 12, "50 kg", null),
            new Ejercicio("Remo de cara con polea", 3, 15, "20 kg", null)
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

        crearRutina("Rutina Pecho - Circuito Quema", "Principiante", "35 min", "Torso", "Perder Peso", null, List.of(
            new Ejercicio("Flexiones diamante", 3, 12, "peso corporal", null),
            new Ejercicio("Press con mancuernas alterno", 3, 14, "12 kg", null),
            new Ejercicio("Fondos entre bancos", 3, 12, "peso corporal", null),
            new Ejercicio("Cruce de poleas", 3, 18, "7 kg", null)
        ));

        crearRutina("Rutina Pierna - Cardio Fuerte", "Principiante", "40 min", "Tren Inferior", "Perder Peso", null, List.of(
            new Ejercicio("Sentadilla con salto", 3, 15, "peso corporal", null),
            new Ejercicio("Zancadas laterales", 3, 12, "8 kg", null),
            new Ejercicio("Burpees", 3, 12, "peso corporal", null),
            new Ejercicio("Step ups en banco", 3, 15, "10 kg", null),
            new Ejercicio("Plancha con elevación de pierna", 3, 20, "peso corporal", null)
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

        crearRutina("Rutina Pecho - Potencia Pura", "Avanzado", "45 min", "Torso", "Hacerse más fuerte", null, List.of(
            new Ejercicio("Press de banca explosivo", 5, 3, "70 kg", null),
            new Ejercicio("Press inclinado pesado", 5, 5, "65 kg", null),
            new Ejercicio("Flexiones con palmada", 4, 6, "peso corporal", null),
            new Ejercicio("Press con mancuernas a una mano", 4, 6, "30 kg", null)
        ));

        crearRutina("Rutina Fuerza - Press y Sentadilla", "Avanzado", "55 min", "Torso", "Hacerse más fuerte", null, List.of(
            new Ejercicio("Sentadilla frontal pesada", 5, 5, "100 kg", null),
            new Ejercicio("Press de banca estricto", 5, 5, "75 kg", null),
            new Ejercicio("Peso muerto sumo", 4, 5, "130 kg", null),
            new Ejercicio("Press militar pesado", 4, 6, "45 kg", null)
        ));

        System.out.println("16 rutinas con ejercicios creadas exitosamente.");
    }

    private void seedProductos() {
        if (productoRepository.count() > 0) return;

        java.util.List<Producto> productos = java.util.List.of(
            crearProducto("Whey Protein Isolate 2lb", "Proteína de suero aislada de rápida absorción. Ideal post-entreno.", 39.99,
                "https://m.media-amazon.com/images/I/71p4bGUMKaL._AC_SL1500_.jpg", "Proteínas", 50),
            crearProducto("Whey Protein 5lb", "Proteína de suero concentrada. Ahorro en presentación familiar.", 69.99,
                "https://m.media-amazon.com/images/I/71QzY2mE5tL._AC_SL1500_.jpg", "Proteínas", 30),
            crearProducto("Creatina Monohidratada 500g", "Creatina pura en polvo para fuerza y rendimiento.", 29.99,
                "https://m.media-amazon.com/images/I/81KqLwHJPzL._AC_SL1500_.jpg", "Creatina", 40),
            crearProducto("Pre-Entreno Explosivo 300g", "Fórmula completa con cafeína, beta-alanina y citrulina.", 34.99,
                "https://m.media-amazon.com/images/I/71Y0gAFFMfL._AC_SL1500_.jpg", "Pre-Entreno", 25),
            crearProducto("Multivitamínico Deportivo 90 tabs", "Complejo B, zinc, magnesio y vitamina D para atletas.", 19.99,
                "https://m.media-amazon.com/images/I/71ojnT3oz6L._AC_SL1500_.jpg", "Vitaminas", 60),
            crearProducto("Barra de Proteína 12 pack", "Snack alto en proteína con 20g por barra. Surtido.", 24.99,
                "https://m.media-amazon.com/images/I/81eZbKLKfmL._AC_SL1500_.jpg", "Proteínas", 100),
            crearProducto("Creatina HCL 120 caps", "Creatina clorhidrato de alta absorción en cápsulas.", 27.99,
                "https://m.media-amazon.com/images/I/71VXFeq4MvL._AC_SL1500_.jpg", "Creatina", 35),
            crearProducto("Shaker 700ml", "Shaker con mezclador de acero inoxidable. Libre de BPA.", 12.99,
                "https://m.media-amazon.com/images/I/61wXe6tT9TL._AC_SL1500_.jpg", "Accesorios", 80),
            crearProducto("Guantes de Gimnasio", "Guantes con soporte de muñeca y gel antideslizante.", 15.99,
                "https://m.media-amazon.com/images/I/71b2GZZ3lCL._AC_SL1500_.jpg", "Accesorios", 45),
            crearProducto("Camiseta Deportiva", "Camiseta dry-fit de manga corta. Varios colores.", 22.99,
                "https://m.media-amazon.com/images/I/71hVpWcFGzL._AC_SL1500_.jpg", "Ropa", 70),
            crearProducto("Short de Entreno", "Short ligero con bolsillos y cintura elástica.", 19.99,
                "https://m.media-amazon.com/images/I/81EKBWB0bDL._AC_SL1500_.jpg", "Ropa", 50),
            crearProducto("BCAA 2:1:1 400 caps", "Aminoácidos ramificados para recuperación muscular.", 25.99,
                "https://m.media-amazon.com/images/I/71KH0bK1YXL._AC_SL1500_.jpg", "Vitaminas", 30)
        );
        for (Producto p : productos) {
            productoRepository.save(p);
        }
        System.out.println("12 productos de ejemplo creados.");
    }

    private Producto crearProducto(String nombre, String descripcion, double precio, String imagenUrl, String categoria, int stock) {
        Producto p = new Producto();
        p.setNombre(nombre);
        p.setDescripcion(descripcion);
        p.setPrecio(precio);
        p.setImagenUrl(imagenUrl);
        p.setCategoria(categoria);
        p.setStock(stock);
        p.setActivo(true);
        return p;
    }

    private void crearRutina(String nombre, String nivel, String tiempo, String grupoMuscular,
                             String objetivoAsociado, String usuarioEmail, List<Ejercicio> ejercicios) {
        if (rutinaRepository.existsByNombre(nombre)) return;
        Rutina rutina = new Rutina(nombre, nivel, tiempo, grupoMuscular, objetivoAsociado, null, usuarioEmail);
        for (Ejercicio e : ejercicios) {
            e.setRutina(rutina);
        }
        rutina.setListaEjercicios(ejercicios);
        rutinaRepository.save(rutina);
    }
}
