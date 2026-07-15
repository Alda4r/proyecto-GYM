package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.HistorialEntrenamiento;
import com.example.demo.model.RecomendacionNutricional;
import com.example.demo.model.Usuario;
import com.example.demo.service.HistorialService;
import com.example.demo.service.RecomendacionService;
import com.example.demo.service.UsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AdminController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private HistorialService historialService;

    @Autowired
    private RecomendacionService recomendacionService;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) {
            return "redirect:/login";
        }

        if ("admin@gym.com".equalsIgnoreCase(user.getEmail())) {
            return "redirect:/admin";
        }

        model.addAttribute("user", user);

        List<HistorialEntrenamiento> hist = historialService.findByUsuarioEmailOrderByFechaHoraDesc(user.getEmail());
        model.addAttribute("totalEntrenamientos", hist.size());
        model.addAttribute("entrenamientosRecientes", hist);
        model.addAttribute("totalCalorias", hist.stream().mapToInt(HistorialEntrenamiento::getCaloriasQuemadas).sum());
        model.addAttribute("racha", calcularRacha(hist));

        return "dashboard";
    }

    @GetMapping("/admin")
    public String adminPanel(HttpSession session, Model model) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null || !"admin@gym.com".equalsIgnoreCase(user.getEmail())) {
            return "redirect:/login";
        }

        List<Usuario> todos = usuarioService.findAll();
        /*
         * Excluir al admin de la lista para que no pueda
         * auto-gestionar su membresía.
         */
        List<Usuario> usuarios = todos.stream()
            .filter(u -> !"admin@gym.com".equalsIgnoreCase(u.getEmail()))
            .toList();

        List<HistorialEntrenamiento> todosHist = historialService.findAll();

        long usuariosActivos = todos.stream()
            .filter(u -> u.getMembresia() != null
            && !u.getMembresia().equalsIgnoreCase("Inactivo"))
            .count();

        model.addAttribute("user", user);
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("totalUsuarios", todos.size());
        model.addAttribute("usuariosActivos", usuariosActivos);
        model.addAttribute("totalEntrenamientos", todosHist.size());
        model.addAttribute("totalCalorias", todosHist.stream().mapToInt(HistorialEntrenamiento::getCaloriasQuemadas).sum());
        model.addAttribute("entrenamientosRecientes", todosHist.stream().limit(5).toList());

        return "admin-dashboard";
    }

    @PostMapping("/admin/actualizar-membresia")
    public String actualizarMembresia(@RequestParam("email") String email,
            @RequestParam("membresia") String nuevaMembresia,
            HttpSession session) {

        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null || !usuarioLogueado.getEmail().equalsIgnoreCase("admin@gym.com")) {
            return "redirect:/login";
        }

        // Buscamos al usuario afectado
        Usuario usuario = usuarioService.findByEmail(email).orElse(null);
        if (usuario != null) {

            if ("Eliminar".equalsIgnoreCase(nuevaMembresia)) {
                if (!"admin@gym.com".equalsIgnoreCase(email)) {
                    usuarioService.deleteByEmail(email);
                }
            } else {
                usuario.setMembresia(nuevaMembresia);

                if (usuario.getMetaCalorias() < 1200) {
                    usuario.setMetaCalorias(1200);
                }

                usuarioService.save(usuario);
            }
        }

        return "redirect:/admin";
    }

    @PostMapping("/admin/registrar-alimento")
    public String registrarAlimento(@RequestParam("nombre") String nombre,
            @RequestParam("calorias") int calorias,
            @RequestParam("tipoComida") String tipoComida,
            @RequestParam("usuarioEmail") String usuarioEmail,
            HttpSession session) {

        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuarioLogueado == null || !usuarioLogueado.getEmail().equalsIgnoreCase("admin@gym.com")) {
            return "redirect:/login";
        }

        RecomendacionNutricional nuevaReceta = new RecomendacionNutricional(nombre, calorias, tipoComida, usuarioEmail);
        recomendacionService.save(nuevaReceta);
        return "redirect:/calorias";
    }

    @GetMapping("/admin/eliminar-sugerencia")
    public String eliminarSugerencia(@RequestParam("id") Long id,
            HttpSession session) {

        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuarioLogueado == null || !"admin@gym.com".equalsIgnoreCase(usuarioLogueado.getEmail())) {
            return "redirect:/login";
        }

        recomendacionService.deleteById(id);

        return "redirect:/calorias";
    }

        private int calcularRacha(List<HistorialEntrenamiento> historial) {
        if (historial == null || historial.isEmpty()) return 0;

        java.util.Set<java.time.LocalDate> fechas = new java.util.HashSet<>();
        for (HistorialEntrenamiento h : historial) {
            fechas.add(h.getFechaHora().toLocalDate());
        }

        java.util.List<java.time.LocalDate> ordenadas = new java.util.ArrayList<>(fechas);
        java.util.Collections.sort(ordenadas, java.util.Collections.reverseOrder());

        if (ordenadas.isEmpty() || !ordenadas.get(0).equals(java.time.LocalDate.now())) {
            return 0;
        }

        int racha = 0;
        java.time.LocalDate esperada = java.time.LocalDate.now();

        for (java.time.LocalDate fecha : ordenadas) {
            if (fecha.equals(esperada)) {
                racha++;
                esperada = esperada.minusDays(1);
            } else {
                break;
            }
        }

        return racha;
    }
}
