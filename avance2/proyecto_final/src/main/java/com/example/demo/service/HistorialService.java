package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.model.HistorialEntrenamiento;
import com.example.demo.repository.HistorialRepository;

@Service
public class HistorialService {

    private final HistorialRepository historialRepository;

    public HistorialService(HistorialRepository historialRepository) {
        this.historialRepository = historialRepository;
    }

    public List<HistorialEntrenamiento> findByUsuarioEmailOrderByFechaHoraDesc(String usuarioEmail) {
        return historialRepository.findByUsuarioEmailOrderByFechaHoraDesc(usuarioEmail);
    }

    public List<HistorialEntrenamiento> findByUsuarioEmail(String usuarioEmail) {
        return historialRepository.findByUsuarioEmail(usuarioEmail);
    }

    public long count() {
        return historialRepository.count();
    }

    public long countByUsuarioEmail(String usuarioEmail) {
        return historialRepository.countByUsuarioEmail(usuarioEmail);
    }

    public HistorialEntrenamiento save(HistorialEntrenamiento historial) {
        return historialRepository.save(historial);
    }

    public void deleteById(Long id) {
        historialRepository.deleteById(id);
    }

    public Optional<HistorialEntrenamiento> findById(Long id) {
        return historialRepository.findById(id);
    }

    public List<HistorialEntrenamiento> findAll() {
        return historialRepository.findAll();
    }
}
