package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.RecomendacionNutricional;

@Repository
public interface RecomendacionRepository extends JpaRepository<RecomendacionNutricional, Long> {
    
    // Genera automáticamente: SELECT * FROM entrenador_alimentos WHERE usuario_email = ?
    List<RecomendacionNutricional> findByUsuarioEmail(String usuarioEmail);
}