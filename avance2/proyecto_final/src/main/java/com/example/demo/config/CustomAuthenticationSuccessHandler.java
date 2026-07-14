package com.example.demo.config;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UsuarioRepository usuarioRepository;

    public CustomAuthenticationSuccessHandler(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findById(email).orElse(null);

        if (usuario == null) {
            response.sendRedirect("/login?error");
            return;
        }

        if (usuario.isSuspendido()) {
            response.sendRedirect("/login?suspendido");
            return;
        }

        if (usuario.getMembresia() != null && usuario.getMembresia().equalsIgnoreCase("Inactivo")) {
            response.sendRedirect("/login?inactivo");
            return;
        }

        HttpSession session = request.getSession();
        session.setAttribute("usuarioLogueado", usuario);

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin || "admin@gym.com".equalsIgnoreCase(email)) {
            response.sendRedirect("/admin");
        } else {
            response.sendRedirect("/dashboard");
        }
    }
}
