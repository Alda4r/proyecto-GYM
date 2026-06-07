package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.HistorialEntrenamiento;

@Repository
public interface HistorialRepository extends JpaRepository<HistorialEntrenamiento, Long> {
    
    // CORRECCIÓN DE ORO: El nombre del método debe coincidir exactamente con la propiedad usuarioEmail
    List<HistorialEntrenamiento> findByUsuarioEmailOrderByFechaHoraDesc(String usuarioEmail);
    List<HistorialEntrenamiento> findByUsuarioEmail(String usuarioEmail);
    long countByUsuarioEmail(String usuarioEmail);
}