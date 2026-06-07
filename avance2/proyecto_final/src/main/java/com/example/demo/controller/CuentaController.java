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
import com.example.demo.repository.HistorialRepository;
import com.example.demo.repository.UsuarioRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class CuentaController {

    @Autowired
    private HistorialRepository historialRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/cuenta")
    public String verCuenta(HttpSession session, Model model) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuarioLogueado != null) {
            Usuario usuarioActivo = usuarioRepository.findById(usuarioLogueado.getEmail()).orElse(usuarioLogueado);
            model.addAttribute("user", usuarioActivo);
            boolean esAdmin = "admin@gym.com".equalsIgnoreCase(usuarioActivo.getEmail());
            model.addAttribute("esAdmin", esAdmin);

            if (!esAdmin) {
                // Cálculo dinámico de IMC
                if (usuarioActivo.getAltura() != null && usuarioActivo.getAltura() > 0 && usuarioActivo.getPesoActual() != null) {
                    double imc = usuarioActivo.getPesoActual() / (usuarioActivo.getAltura() * usuarioActivo.getAltura());
                    model.addAttribute("imcValue", String.format("%.1f", imc));
                    
                    String clasificacion = "Normal";
                    if (imc < 18.5) clasificacion = "Bajo Peso";
                    else if (imc >= 25 && imc < 30) clasificacion = "Sobrepeso";
                    else if (imc >= 30) clasificacion = "Obesidad";
                    model.addAttribute("imcClasificacion", clasificacion);
                }
            } else {
                List<Usuario> usuarios = usuarioRepository.findAll();
                long totalUsuarios = usuarios.size();
                long usuariosActivos = usuarios.stream()
                        .filter(u -> u.getMembresia() != null && !u.getMembresia().equalsIgnoreCase("Inactivo"))
                        .count();
                long usuariosPremium = usuarios.stream()
                        .filter(u -> u.getMembresia() != null && u.getMembresia().equalsIgnoreCase("Premium"))
                        .count();
                long totalEntrenamientos = historialRepository.count();

                model.addAttribute("adminTotalUsuarios", totalUsuarios);
                model.addAttribute("adminUsuariosActivos", usuariosActivos);
                model.addAttribute("adminUsuariosPremium", usuariosPremium);
                model.addAttribute("adminEntrenamientosTotales", totalEntrenamientos);
            }

            // Cargar historial ordenado desde SQL Server
            List<HistorialEntrenamiento> ultimasRutinas = historialRepository.findByUsuarioEmailOrderByFechaHoraDesc(usuarioActivo.getEmail());
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
    public String editarPerfil(@Valid @ModelAttribute("user") Usuario usuarioFormulario, 
                               BindingResult result, 
                               HttpSession session, 
                               Model model) {
        
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/login";
        }

        // Si hay errores de validación, recargamos la vista sin guardar
        if (result.hasErrors()) {
            if (usuarioLogueado.getAltura() != null && usuarioLogueado.getAltura() > 0 && usuarioLogueado.getPesoActual() != null) {
                double imc = usuarioLogueado.getPesoActual() / (usuarioLogueado.getAltura() * usuarioLogueado.getAltura());
                model.addAttribute("imcValue", String.format("%.1f", imc));
                model.addAttribute("imcClasificacion", imc < 18.5 ? "Bajo Peso" : (imc >= 25 ? "Sobrepeso" : "Normal"));
            }
            
            List<HistorialEntrenamiento> ultimasRutinas = historialRepository.findByUsuarioEmailOrderByFechaHoraDesc(usuarioLogueado.getEmail());
            model.addAttribute("historialReal", (ultimasRutinas != null) ? ultimasRutinas : new java.util.ArrayList<>());
            
            model.addAttribute("mostrarModalErrores", true);
            return "cuenta"; 
        }

        // SI TODO ESTÁ BIEN: Guardamos los parámetros fisiológicos y el NUEVO OBJETIVO
        Usuario usuario = usuarioRepository.findById(usuarioLogueado.getEmail()).orElse(null);
        if (usuario != null) {
            usuario.setPesoActual(usuarioFormulario.getPesoActual());
            usuario.setAltura(usuarioFormulario.getAltura());
            usuario.setMetaCalorias(usuarioFormulario.getMetaCalorias());
            usuario.setObjetivo(usuarioFormulario.getObjetivo()); // <--- Línea clave añadida
            
            usuarioRepository.save(usuario);
            
            // Sincronizamos la sesión con el nuevo objetivo
            session.setAttribute("usuarioLogueado", usuario);
        }

        return "redirect:/cuenta";
    }
}