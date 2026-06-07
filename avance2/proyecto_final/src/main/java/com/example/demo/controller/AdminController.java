package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.HistorialEntrenamiento;
import com.example.demo.model.RecomendacionNutricional; // Importación añadida
import com.example.demo.model.Usuario;
import com.example.demo.repository.HistorialRepository;
import com.example.demo.repository.RecomendacionRepository; // Importación añadida
import com.example.demo.repository.UsuarioRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class AdminController {

    @Autowired 
    private UsuarioRepository usuarioRepository;
    
    @Autowired 
    private HistorialRepository historialRepository;

    @Autowired 
    private RecomendacionRepository recomendacionRepository; // Inyección añadida

    // ==========================================
    // 1. DASHBOARD ALUMNO
    // ==========================================
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) return "redirect:/login";

        if ("admin@gym.com".equalsIgnoreCase(user.getEmail())) {
            return "redirect:/admin";
        }

        model.addAttribute("user", user);

        List<HistorialEntrenamiento> hist = historialRepository.findByUsuarioEmailOrderByFechaHoraDesc(user.getEmail());
        model.addAttribute("totalEntrenamientos", hist.size());
        model.addAttribute("entrenamientosRecientes", hist);
        model.addAttribute("totalCalorias", hist.stream().mapToInt(HistorialEntrenamiento::getCaloriasQuemadas).sum());
        model.addAttribute("racha", hist.isEmpty() ? 0 : 1);
        
        return "dashboard";
    }

    // ==========================================
    // 2. PANEL ADMINISTRADOR
    // ==========================================
    @GetMapping("/admin")
    public String adminPanel(HttpSession session, Model model) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null || !"admin@gym.com".equalsIgnoreCase(user.getEmail())) {
            return "redirect:/login";
        }

        List<Usuario> usuarios = usuarioRepository.findAll();
        List<HistorialEntrenamiento> todosHist = historialRepository.findAll();

        long usuariosActivos = usuarios.stream()
                .filter(u -> "Activo".equalsIgnoreCase(u.getMembresia()) 
                          || "Premium".equalsIgnoreCase(u.getMembresia()) 
                          || "Regular".equalsIgnoreCase(u.getMembresia()) 
                          || "Digital-Gratis".equalsIgnoreCase(u.getMembresia()))
                .count();

        model.addAttribute("user", user);
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("totalUsuarios", usuarios.size());
        model.addAttribute("usuariosPremium", usuariosActivos); 
        model.addAttribute("totalEntrenamientos", todosHist.size());
        model.addAttribute("totalCalorias", todosHist.stream().mapToInt(HistorialEntrenamiento::getCaloriasQuemadas).sum());
        model.addAttribute("entrenamientosRecientes", todosHist.stream().limit(5).toList());

        return "admin-dashboard";
    }

    // ==========================================
    // 3. ACTUALIZAR ESTADO (3 VÍAS: ACTIVO, INACTIVO, ELIMINAR)
    // ==========================================
    @PostMapping("/admin/actualizar-membresia")
    public String actualizarMembresia(@RequestParam("email") String email,
            @RequestParam("membresia") String nuevaMembresia,
            HttpSession session) {
        
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null || !usuarioLogueado.getEmail().equalsIgnoreCase("admin@gym.com")) {
            return "redirect:/login";
        }

        // Buscamos al usuario afectado
        Usuario usuario = usuarioRepository.findById(email).orElse(null);
        if (usuario != null) {
            
            // ACCIÓN 1: Si se selecciona "Eliminar", se borra físicamente de la base de datos de inmediato
            if ("Eliminar".equalsIgnoreCase(nuevaMembresia)) {
                // ESCUDO DE SEGURIDAD: Evitar que el admin se elimine a sí mismo por error
                if (!"admin@gym.com".equalsIgnoreCase(email)) {
                    usuarioRepository.delete(usuario);
                }
            } 
            // ACCIÓN 2: Si es "Activo" o "Inactivo", simplemente se guarda el nuevo estado
            else {
                usuario.setMembresia(nuevaMembresia);
                
                // Parche preventivo para el error 500 de validación de calorías
                if (usuario.getMetaCalorias() < 1200) {
                    usuario.setMetaCalorias(1200);
                }
                
                usuarioRepository.save(usuario);
            }
        }

        return "redirect:/admin";
    }

    // ==========================================
    // 4. REGISTRAR DIETA / ALIMENTO PARA EL ALUMNO
    // ==========================================
    @PostMapping("/admin/registrar-alimento")
    public String registrarAlimento(@RequestParam("nombre") String nombre,
            @RequestParam("calorias") int calorias,
            @RequestParam("tipoComida") String tipoComida,
            @RequestParam("usuarioEmail") String usuarioEmail,
            HttpSession session) {

        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        
        // Candado de seguridad para el Administrador
        if (usuarioLogueado == null || !usuarioLogueado.getEmail().equalsIgnoreCase("admin@gym.com")) {
            return "redirect:/login";
        }

        // Creamos la recomendación amarrada al correo del alumno elegido en el selector
        RecomendacionNutricional nuevaReceta = new RecomendacionNutricional(nombre, calorias, tipoComida, usuarioEmail);
        recomendacionRepository.save(nuevaReceta);

        // Redirige de vuelta a la vista unificada de calorías
        return "redirect:/calorias"; 
    }
}