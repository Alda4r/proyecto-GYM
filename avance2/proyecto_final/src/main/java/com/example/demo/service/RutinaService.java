package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.model.Rutina;
import com.example.demo.repository.RutinaRepository;

@Service
public class RutinaService {

    private final RutinaRepository rutinaRepository;

    public RutinaService(RutinaRepository rutinaRepository) {
        this.rutinaRepository = rutinaRepository;
    }
    public List<Rutina> findAll() {
        return rutinaRepository.findAll();
    }
    public Optional<Rutina> findById(Long id) {
        return rutinaRepository.findById(id);
    }
    public Rutina save(Rutina rutina) {
        return rutinaRepository.save(rutina);
    }
    public void deleteById(Long id) {
        rutinaRepository.deleteById(id);
    }
    public boolean existsByNombre(String nombre) {
        return rutinaRepository.existsByNombre(nombre);
    }
    public List<Rutina> findByGrupoMuscular(String grupoMuscular) {
        return rutinaRepository.findByGrupoMuscular(grupoMuscular);
    }
    public List<Rutina> findByObjetivoAsociado(String objetivoAsociado) {
        return rutinaRepository.findByObjetivoAsociado(objetivoAsociado);
    }
    public List<Rutina> findByUsuarioEmail(String usuarioEmail) {
        return rutinaRepository.findByUsuarioEmail(usuarioEmail);
    }
}
