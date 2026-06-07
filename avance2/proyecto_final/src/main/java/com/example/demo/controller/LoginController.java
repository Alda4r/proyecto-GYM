package com.example.demo.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;

import jakarta.servlet.http.HttpSession; // IMPORTANTE: Para manejar la memoria de sesión

@Controller
public class LoginController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // 1. Muestra la pantalla de inicio de sesión
    @GetMapping("/login")
    public String mostrarLogin() {
        return "login"; // Devuelve la vista login.html
    }

    // 2. Procesa los datos del formulario al hacer clic en "Ingresar"
    @PostMapping("/login")
    public String procesarLogin(@RequestParam("email") String email, 
                                @RequestParam("password") String password, 
                                HttpSession session, // AGREGADO: Inyectamos la sesión aquí
                                Model model) {
        
        // Ejecuta la consulta en SQL Server
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmailAndPassword(email, password);

        if (usuarioOpt.isPresent()) {
            // Extraemos el objeto Usuario real que estaba dentro del Optional
            Usuario usuarioReal = usuarioOpt.get();

            if (usuarioReal.isSuspendido()) {
                return "redirect:/login?suspendido";
            }
            
            if (usuarioReal.getMembresia() != null && usuarioReal.getMembresia().equalsIgnoreCase("Inactivo")) {
                return "redirect:/login?inactivo";
            }
            
            // GUARDADO EN SESIÓN: El servidor recordará a este usuario con el apodo "usuarioLogueado"
            session.setAttribute("usuarioLogueado", usuarioReal);
            
            // Si es admin, lo llevamos directo al panel de gestión
            if ("admin@gym.com".equalsIgnoreCase(usuarioReal.getEmail())) {
                return "redirect:/admin";
            }
            
            // ¡Éxito! Redirige al Dashboard de forma segura
            return "redirect:/dashboard"; 
        } else {
            // Error: Redirige al login agregando el parámetro "?error" en la URL
            return "redirect:/login?error";
        }
    }
}