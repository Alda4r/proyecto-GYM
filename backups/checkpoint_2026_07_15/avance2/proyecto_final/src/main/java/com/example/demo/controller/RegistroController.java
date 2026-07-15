package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.Usuario;
import com.example.demo.service.UsuarioService;

import jakarta.validation.Valid;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegistroController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/registro")
    public String registro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    @PostMapping("/registro")
    public String registrarUsuario(@Valid @ModelAttribute("usuario") Usuario usuario,
            BindingResult result,
            @RequestParam("confirmarPassword") String confirmarPassword,
            Model model,
            RedirectAttributes redirectAttrs) {

        if (usuarioService.existsByEmail(usuario.getEmail())) {
            result.rejectValue("email", "error.usuario", "Ya existe una cuenta con este correo.");
        }

        if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
            result.rejectValue("password", "error.usuario", "La contraseña es obligatoria.");
        }

        if (!usuario.getPassword().equals(confirmarPassword)) {
            result.rejectValue("password", "error.usuario", "Las contraseñas no coinciden.");
        }

        if (result.hasErrors()) {
            return "registro";
        }

        usuario.setRol("USER");
        usuario.setMembresia("Activo");
        usuarioService.save(usuario);

        redirectAttrs.addFlashAttribute("exito", "¡Registrado correctamente! Ya puedes iniciar sesión.");
        return "redirect:/login";
    }
}
