package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.PlanMembresia;

public interface PlanMembresiaRepository extends JpaRepository<PlanMembresia, Long> {
    List<PlanMembresia> findByActivoTrueOrderByDuracionMesesAsc();
}
