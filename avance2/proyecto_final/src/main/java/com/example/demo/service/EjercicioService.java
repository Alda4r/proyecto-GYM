package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.model.Ejercicio;
import com.example.demo.repository.EjercicioRepository;

@Service
public class EjercicioService {

    private final EjercicioRepository ejercicioRepository;

    public EjercicioService(EjercicioRepository ejercicioRepository) {
        this.ejercicioRepository = ejercicioRepository;
    }

    public List<Ejercicio> findByRutinaId(Long rutinaId) {
        return ejercicioRepository.findByRutinaId(rutinaId);
    }

    public Optional<Ejercicio> findById(Long id) {
        return ejercicioRepository.findById(id);
    }

    public Ejercicio save(Ejercicio ejercicio) {
        return ejercicioRepository.save(ejercicio);
    }

    public List<Ejercicio> saveAll(List<Ejercicio> ejercicios) {
        return ejercicioRepository.saveAll(ejercicios);
    }

    public List<Ejercicio> findAll() {
        return ejercicioRepository.findAll();
    }

    public void deleteById(Long id) {
        ejercicioRepository.deleteById(id);
    }
}
