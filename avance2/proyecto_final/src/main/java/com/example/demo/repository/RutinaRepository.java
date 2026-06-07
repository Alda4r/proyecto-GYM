package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Rutina;

@Repository
public interface RutinaRepository extends JpaRepository<Rutina, Long> {
    
    // El método que le faltaba a tu controlador original para armar las listas
    List<Rutina> findByGrupoMuscular(String grupoMuscular);
    
    // El método nuevo que añadimos para hacer el filtro inteligente por metas
    List<Rutina> findByObjetivoAsociado(String objetivoAsociado);
}