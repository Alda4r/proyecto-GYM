package com.example.demo.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "historial_entrenamientos")
public class HistorialEntrenamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String usuarioEmail; // Para saber qué atleta completó el ejercicio
    private String rutinaNombre;
    private int caloriasQuemadas;
    private int duracionMinutos;
    private LocalDateTime fechaHora;

    public HistorialEntrenamiento() {}

    public HistorialEntrenamiento(String usuarioEmail, String rutinaNombre, int caloriasQuemadas, int duracionMinutos) {
        this.usuarioEmail = usuarioEmail;
        this.rutinaNombre = rutinaNombre;
        this.caloriasQuemadas = caloriasQuemadas;
        this.duracionMinutos = duracionMinutos;
        this.fechaHora = LocalDateTime.now(); // Se guarda la hora exacta del clic
    }

    // ========== GETTERS Y SETTERS ==========
    public Long getId() { return id; }
    public String getUsuarioEmail() { return usuarioEmail; }
    public String getRutinaNombre() { return rutinaNombre; }
    public int getCaloriasQuemadas() { return caloriasQuemadas; }
    public int getDuracionMinutos() { return duracionMinutos; }
    public LocalDateTime getFechaHora() { return fechaHora; }
}