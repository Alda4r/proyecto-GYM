package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.model.RecomendacionNutricional;
import com.example.demo.repository.RecomendacionRepository;

@Service
public class RecomendacionService {

    private final RecomendacionRepository recomendacionRepository;

    public RecomendacionService(RecomendacionRepository recomendacionRepository) {
        this.recomendacionRepository = recomendacionRepository;
    }

    public List<RecomendacionNutricional> findByUsuarioEmail(String usuarioEmail) {
        return recomendacionRepository.findByUsuarioEmail(usuarioEmail);
    }

    public List<RecomendacionNutricional> findAll() {
        return recomendacionRepository.findAll();
    }

    public Optional<RecomendacionNutricional> findById(Long id) {
        return recomendacionRepository.findById(id);
    }

    public RecomendacionNutricional save(RecomendacionNutricional recomendacion) {
        return recomendacionRepository.save(recomendacion);
    }

    public void deleteById(Long id) {
        recomendacionRepository.deleteById(id);
    }
}
