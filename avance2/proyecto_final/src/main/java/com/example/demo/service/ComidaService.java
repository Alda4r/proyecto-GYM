package com.example.demo.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.model.Comida;
import com.example.demo.repository.ComidaRepository;

@Service
public class ComidaService {

    private final ComidaRepository comidaRepository;

    public ComidaService(ComidaRepository comidaRepository) {
        this.comidaRepository = comidaRepository;
    }

    public List<Comida> findByUsuarioEmailAndFecha(String usuarioEmail, LocalDate fecha) {
        return comidaRepository.findByUsuarioEmailAndFecha(usuarioEmail, fecha);
    }

    public Comida save(Comida comida) {
        return comidaRepository.save(comida);
    }

    public List<Comida> findAll() {
        return comidaRepository.findAll();
    }

    public Optional<Comida> findById(Long id) {
        return comidaRepository.findById(id);
    }

    public List<Comida> findByUsuarioEmail(String usuarioEmail) {
        return comidaRepository.findByUsuarioEmail(usuarioEmail);
    }

    public void deleteById(Long id) {
        comidaRepository.deleteById(id);
    }
}
