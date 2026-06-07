package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;

@Controller
public class RegistroController {
    @Autowired
    private UsuarioRepository usuarioRepository;
    @GetMapping("/registro")
    public String registro(org.springframework.ui.Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }
    @PostMapping("/registro")
    public String registrarUsuario(@ModelAttribute Usuario usuario) {
        usuarioRepository.save(usuario);
        
        return "redirect:/login?exito"; 
    }
}