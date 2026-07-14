package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Optional<Usuario> authenticate(String email, String password) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(email);

        if (usuarioOpt.isEmpty()) {
            return Optional.empty();
        }

        Usuario usuario = usuarioOpt.get();

        if (passwordEncoder.matches(password, usuario.getPassword())) {
            return Optional.of(usuario);
        }

        return Optional.empty();
    }

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findById(email);
    }

    public boolean existsByEmail(String email) {
        return usuarioRepository.existsById(email);
    }

    public Usuario save(Usuario usuario) {
        if (usuario.getPassword() != null && !esPasswordEncriptado(usuario.getPassword())) {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }

        return usuarioRepository.save(usuario);
    }

    public void deleteByEmail(String email) {
        usuarioRepository.deleteById(email);
    }

    private boolean esPasswordEncriptado(String password) {
        return password.startsWith("$2a$")
                || password.startsWith("$2b$")
                || password.startsWith("$2y$");
    }
}