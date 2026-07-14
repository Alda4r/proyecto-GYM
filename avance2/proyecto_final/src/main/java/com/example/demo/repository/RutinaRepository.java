package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Rutina;

@Repository
public interface RutinaRepository extends JpaRepository<Rutina, Long> {
    

    List<Rutina> findByGrupoMuscular(String grupoMuscular);

    List<Rutina> findByObjetivoAsociado(String objetivoAsociado);

    List<Rutina> findByUsuarioEmail(String usuarioEmail);
}