package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {
    
    // Spring Data JPA creará la consulta SELECT automáticamente
    Optional<Usuario> findByEmailAndPassword(String email, String password);
}