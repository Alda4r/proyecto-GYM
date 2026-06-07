package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Ejercicio;

@Repository
public interface EjercicioRepository extends JpaRepository<Ejercicio, Long> {
    
    // JPA leerá el objeto "rutina" dentro de Ejercicio y buscará por su ID
    List<Ejercicio> findByRutinaId(Long rutinaId);
}