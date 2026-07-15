package com.example.demo.config;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.service.CarritoService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UsuarioRepository usuarioRepository;
    private final CarritoService carritoService;

    public CustomAuthenticationSuccessHandler(UsuarioRepository usuarioRepository, CarritoService carritoService) {
        this.usuarioRepository = usuarioRepository;
        this.carritoService = carritoService;
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

        boolean isAdmin = "admin@gym.com".equalsIgnoreCase(email);
        session.setAttribute("carritoCount", isAdmin ? 0 : carritoService.getCartCount(email));

        if (isAdmin) {
            response.sendRedirect("/admin");
        } else {
            response.sendRedirect("/menu");
        }
    }
}
