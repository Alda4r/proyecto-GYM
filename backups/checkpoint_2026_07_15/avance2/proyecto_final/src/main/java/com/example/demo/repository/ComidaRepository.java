package com.example.demo.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Comida;

@Repository
public interface ComidaRepository extends JpaRepository<Comida, Long> {
    List<Comida> findByUsuarioEmailAndFecha(String usuarioEmail, LocalDate fecha);
    List<Comida> findByUsuarioEmail(String usuarioEmail);
}