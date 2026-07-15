package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.PlanMembresia;
import com.example.demo.repository.PlanMembresiaRepository;

@Service
public class PlanMembresiaService {

    @Autowired
    private PlanMembresiaRepository planMembresiaRepository;

    public List<PlanMembresia> findAllActivos() {
        return planMembresiaRepository.findByActivoTrueOrderByDuracionMesesAsc();
    }

    public List<PlanMembresia> findAll() {
        return planMembresiaRepository.findAll();
    }

    public Optional<PlanMembresia> findById(Long id) {
        return planMembresiaRepository.findById(id);
    }

    public PlanMembresia save(PlanMembresia plan) {
        return planMembresiaRepository.save(plan);
    }
}
