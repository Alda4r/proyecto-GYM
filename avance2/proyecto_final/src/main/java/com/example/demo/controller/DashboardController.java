/*package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.model.HistorialEntrenamiento;
import com.example.demo.model.Usuario;
import com.example.demo.repository.HistorialRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class DashboardController {

    @Autowired
    private HistorialRepository historialRepository;

    @GetMapping("/dashboard")
    public String mostrarDashboard(Model model, HttpSession session) {
        
        Usuario usuarioSesion = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuarioSesion == null) {
            return "redirect:/login";
        }

        // IMPORTANTE: Aseguramos que el objeto 'user' esté siempre presente
        // para que en el HTML th:text="${user.nombre}" no falle.
        model.addAttribute("user", usuarioSesion);

        String emailUsuario = usuarioSesion.getEmail();

        // Si es el administrador, redirigimos a su panel especial
        if ("admin@gym.com".equalsIgnoreCase(emailUsuario)) {
            return "redirect:/admin_dashboard"; // O maneja la lógica dentro del AdminController
        }

        // Lógica para alumnos regulares
        long totalEntrenamientos = historialRepository.countByUsuarioEmail(emailUsuario);
        List<HistorialEntrenamiento> listaRecientes = historialRepository.findByUsuarioEmailOrderByFechaHoraDesc(emailUsuario);

        int totalCaloriasQuemadas = listaRecientes.stream()
                .mapToInt(HistorialEntrenamiento::getCaloriasQuemadas)
                .sum();

        int rachaDias = listaRecientes.isEmpty() ? 0 : 1;

        model.addAttribute("totalEntrenamientos", totalEntrenamientos);
        model.addAttribute("entrenamientosRecientes", listaRecientes);
        model.addAttribute("totalCalorias", totalCaloriasQuemadas);
        model.addAttribute("racha", rachaDias);

        return "dashboard"; 
    }
}

*/