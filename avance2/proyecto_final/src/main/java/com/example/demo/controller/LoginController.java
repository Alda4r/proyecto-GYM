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

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam("email") String email, 
                                @RequestParam("password") String password, 
                                HttpSession session,
                                Model model) {
        
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmailAndPassword(email, password);

        if (usuarioOpt.isPresent()) {
            Usuario usuarioReal = usuarioOpt.get();

            if (usuarioReal.isSuspendido()) {
                return "redirect:/login?suspendido";
            }
            
            if (usuarioReal.getMembresia() != null && usuarioReal.getMembresia().equalsIgnoreCase("Inactivo")) {
                return "redirect:/login?inactivo";
            }
            
            session.setAttribute("usuarioLogueado", usuarioReal);
            
            if ("admin@gym.com".equalsIgnoreCase(usuarioReal.getEmail())) {
                return "redirect:/admin";
            }
            
            return "redirect:/dashboard"; 
        } else {
            return "redirect:/login?error";
        }
    }
}