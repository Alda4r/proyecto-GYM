package com.example.demo.controller.api;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.config.JwtUtil;
import com.example.demo.model.Usuario;
import com.example.demo.service.UsuarioService;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

    private final UsuarioService usuarioService;
    private final JwtUtil jwtUtil;

    public ApiAuthController(UsuarioService usuarioService, JwtUtil jwtUtil) {
        this.usuarioService = usuarioService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");

        if (email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email y password requeridos"));
        }

        var usuarioOpt = usuarioService.authenticate(email, password);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas"));
        }

        Usuario user = usuarioOpt.get();
        String token = jwtUtil.generateToken(user.getEmail(), user.getRol());

        return ResponseEntity.ok(Map.of(
            "token", token,
            "email", user.getEmail(),
            "nombre", user.getNombre(),
            "rol", user.getRol()
        ));
    }
}