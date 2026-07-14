package com.example.demo.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.Comida;
import com.example.demo.model.HistorialEntrenamiento;
import com.example.demo.model.Usuario;
import com.example.demo.service.ComidaService;
import com.example.demo.service.HistorialService;
import com.example.demo.service.UsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ProgresoController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ComidaService comidaService;

    @Autowired
    private HistorialService historialService;

    @GetMapping("/progreso")
    public String verProgreso(@RequestParam(value = "buscarEmail", required = false) String buscarEmail,
            HttpSession session, Model model) {

        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/login";
        }

        boolean isAdmin = usuarioLogueado.getEmail().equalsIgnoreCase("admin@gym.com");
        model.addAttribute("isAdmin", isAdmin);

        if (isAdmin) {
            List<Usuario> listaAlumnos = usuarioService.findAll().stream()
                    .filter(u -> !u.getEmail().equalsIgnoreCase("admin@gym.com"))
                    .toList();
            model.addAttribute("alumnos", listaAlumnos);

            if (buscarEmail != null && !buscarEmail.isEmpty()) {
                Usuario alumnoSeleccionado = usuarioService.findByEmail(buscarEmail).orElse(null);
                if (alumnoSeleccionado != null) {
                    cargarMetricasUsuario(alumnoSeleccionado, model);
                    model.addAttribute("alumnoAuditado", alumnoSeleccionado);
                }
            }
        } else {
            cargarMetricasUsuario(usuarioLogueado, model);
        }

        return "progreso";
    }

    private void cargarMetricasUsuario(Usuario usuario, Model model) {
        model.addAttribute("pesoActual", usuario.getPesoActual());
        model.addAttribute("objetivoActual", usuario.getObjetivo());

        // Calcular IMC elemental
        Double pesoActual = usuario.getPesoActual();
        Double altura = usuario.getAltura();

        if (pesoActual != null && altura != null && altura > 0) {
            double imc = pesoActual / (altura * altura);
            model.addAttribute("imc", Math.round(imc * 10.0) / 10.0);
        } else {
            model.addAttribute("imc", 0.0);
        }

        // 1. Conteo de entrenamientos por zonas del historial de este usuario (Evita el null)
        List<HistorialEntrenamiento> historial = historialService.findByUsuarioEmail(usuario.getEmail());

        long pecho = historial.stream().filter(h -> h.getRutinaNombre().toLowerCase().contains("pecho")).count();
        long espalda = historial.stream().filter(h -> h.getRutinaNombre().toLowerCase().contains("espalda")).count();
        long pierna = historial.stream().filter(h -> h.getRutinaNombre().toLowerCase().contains("pierna")).count();
        long fuerza = historial.stream().filter(h -> h.getRutinaNombre().toLowerCase().contains("fuerza")).count();

        model.addAttribute("totalEntrenamientos", historial.size());
        model.addAttribute("vecesPecho", pecho);
        model.addAttribute("vecesEspalda", espalda);
        model.addAttribute("vecesPierna", pierna);
        model.addAttribute("vecesFuerza", fuerza);

        // Bases iniciales para evitar errores
        model.addAttribute("grasa", "0.0%");
        model.addAttribute("masaMuscular", "0.0 kg");

        // 2. Auditoría de alimentación del día actual
        List<Comida> comidasHoy = comidaService.findByUsuarioEmailAndFecha(usuario.getEmail(), LocalDate.now());
        int totalCalorias = comidasHoy.stream().mapToInt(Comida::getCalorias).sum();
        int totalProteina = comidasHoy.stream().mapToInt(Comida::getProteina).sum();

        model.addAttribute("caloriasHoy", totalCalorias);
        model.addAttribute("proteinaHoy", totalProteina);
        model.addAttribute("comioHoy", !comidasHoy.isEmpty());
    }
}
