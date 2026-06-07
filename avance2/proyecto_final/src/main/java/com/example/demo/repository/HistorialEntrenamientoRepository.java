package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.HistorialEntrenamiento;

@Repository
public interface HistorialEntrenamientoRepository extends JpaRepository<HistorialEntrenamiento, Long> {
    // Hereda todos los métodos CRUD usando el ID como Long
}