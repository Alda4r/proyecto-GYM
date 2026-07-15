package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.RecomendacionNutricional;

@Repository
public interface RecomendacionRepository extends JpaRepository<RecomendacionNutricional, Long> {
    
    List<RecomendacionNutricional> findByUsuarioEmail(String usuarioEmail);
}