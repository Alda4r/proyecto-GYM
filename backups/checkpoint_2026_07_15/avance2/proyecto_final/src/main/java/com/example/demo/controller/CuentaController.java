package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.model.HistorialEntrenamiento;
import com.example.demo.model.Usuario;
import com.example.demo.service.HistorialService;
import com.example.demo.service.UsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
public class CuentaController {

    @Autowired
    private HistorialService historialService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/cuenta")
    public String verCuenta(HttpSession session, Model model) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuarioLogueado != null) {
            Usuario usuarioActivo = usuarioService.findByEmail(usuarioLogueado.getEmail())
                    .orElse(usuarioLogueado);

            session.setAttribute("usuarioLogueado", usuarioActivo);
            model.addAttribute("user", usuarioActivo);
            model.addAttribute("planActual", usuarioActivo.getPlanMembresia());

            boolean esAdmin = "admin@gym.com".equalsIgnoreCase(usuarioActivo.getEmail());
            model.addAttribute("esAdmin", esAdmin);

            if (!esAdmin) {
                agregarImcAlModelo(usuarioActivo, model);
            } else {
                model.addAttribute("planActual", null);
                List<Usuario> usuarios = usuarioService.findAll();

                long totalUsuarios = usuarios.size();

                long usuariosActivos = usuarios.stream()
                        .filter(u -> u.getMembresia() != null
                                && !u.getMembresia().equalsIgnoreCase("Inactivo"))
                        .count();

                long totalEntrenamientos = historialService.count();

                model.addAttribute("adminTotalUsuarios", totalUsuarios);
                model.addAttribute("adminUsuariosActivos", usuariosActivos);
                model.addAttribute("adminEntrenamientosTotales", totalEntrenamientos);
            }

            List<HistorialEntrenamiento> ultimasRutinas = historialService
                    .findByUsuarioEmailOrderByFechaHoraDesc(usuarioActivo.getEmail());

            if (ultimasRutinas != null && !ultimasRutinas.isEmpty()) {
                if (ultimasRutinas.size() > 5) {
                    ultimasRutinas = ultimasRutinas.subList(0, 5);
                }
            } else {
                ultimasRutinas = new java.util.ArrayList<>();
            }

            model.addAttribute("historialReal", ultimasRutinas);
        } else {
            model.addAttribute("user", null);
        }

        return "cuenta";
    }

    @PostMapping("/cuenta/editar")
    public String editarPerfil(@ModelAttribute("user") Usuario usuarioFormulario,
            BindingResult result,
            HttpSession session,
            Model model) {

        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuarioLogueado == null) {
            return "redirect:/login";
        }

        validarFormularioPerfil(usuarioFormulario, result);

        if (result.hasErrors()) {
            Usuario usuarioActivo = usuarioService.findByEmail(usuarioLogueado.getEmail())
                    .orElse(usuarioLogueado);

            model.addAttribute("user", usuarioFormulario);

            boolean esAdmin = "admin@gym.com".equalsIgnoreCase(usuarioActivo.getEmail());
            model.addAttribute("esAdmin", esAdmin);

            agregarImcAlModelo(usuarioActivo, model);
            agregarHistorialAlModelo(usuarioActivo, model);

            model.addAttribute("mostrarModalErrores", true);

            return "cuenta";
        }

        Usuario usuario = usuarioService.findByEmail(usuarioLogueado.getEmail()).orElse(null);

        if (usuario != null) {
            usuario.setPesoActual(usuarioFormulario.getPesoActual());
            usuario.setAltura(usuarioFormulario.getAltura());
            usuario.setMetaCalorias(usuarioFormulario.getMetaCalorias());
            usuario.setObjetivo(usuarioFormulario.getObjetivo());

            Usuario usuarioActualizado = usuarioService.save(usuario);

            session.setAttribute("usuarioLogueado", usuarioActualizado);
        }

        return "redirect:/cuenta";
    }

    private void validarFormularioPerfil(Usuario usuarioFormulario, BindingResult result) {
        if (usuarioFormulario.getPesoActual() == null || usuarioFormulario.getPesoActual() < 30) {
            result.rejectValue("pesoActual", "error.user", "Ingrese un peso válido, mínimo 30 kg.");
        }

        if (usuarioFormulario.getAltura() == null || usuarioFormulario.getAltura() < 1.0) {
            result.rejectValue("altura", "error.user", "Ingrese una altura válida, mínimo 1.0 m.");
        }

        if (usuarioFormulario.getMetaCalorias() == null || usuarioFormulario.getMetaCalorias() < 1200) {
            result.rejectValue("metaCalorias", "error.user", "La meta calórica debe ser de al menos 1200 kcal.");
        }

        if (usuarioFormulario.getObjetivo() == null || usuarioFormulario.getObjetivo().isBlank()) {
            result.rejectValue("objetivo", "error.user", "El objetivo es obligatorio.");
        }
    }

    private void agregarImcAlModelo(Usuario usuario, Model model) {
        if (usuario.getAltura() != null && usuario.getAltura() > 0 && usuario.getPesoActual() != null) {
            double imc = usuario.getPesoActual() / (usuario.getAltura() * usuario.getAltura());

            model.addAttribute("imcValue", String.format("%.1f", imc));

            String clasificacion = "Normal";

            if (imc < 18.5) {
                clasificacion = "Bajo Peso";
            } else if (imc >= 25 && imc < 30) {
                clasificacion = "Sobrepeso";
            } else if (imc >= 30) {
                clasificacion = "Obesidad";
            }

            model.addAttribute("imcClasificacion", clasificacion);
        }
    }

    private void agregarHistorialAlModelo(Usuario usuario, Model model) {
        List<HistorialEntrenamiento> ultimasRutinas = historialService
                .findByUsuarioEmailOrderByFechaHoraDesc(usuario.getEmail());

        if (ultimasRutinas != null && !ultimasRutinas.isEmpty()) {
            if (ultimasRutinas.size() > 5) {
                ultimasRutinas = ultimasRutinas.subList(0, 5);
            }
        } else {
            ultimasRutinas = new java.util.ArrayList<>();
        }

        model.addAttribute("historialReal", ultimasRutinas);
    }
}