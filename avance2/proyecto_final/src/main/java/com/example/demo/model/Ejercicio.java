package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ejercicios")
public class Ejercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private int series;
    private int repeticiones;
    
    @Column(name = "peso_sugerido")
    private String pesoSugerido;

    @ManyToOne
    @JoinColumn(name = "rutina_id", nullable = false)
    private Rutina rutina;


    public Ejercicio() {
    }


    public Ejercicio(String nombre, int series, int repeticiones, String pesoSugerido, Rutina rutina) {
        this.nombre = nombre;
        this.series = series;
        this.repeticiones = repeticiones;
        this.pesoSugerido = pesoSugerido;
        this.rutina = rutina;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public int getSeries() { return series; }
    public void setSeries(int series) { this.series = series; }
    public int getRepeticiones() { return repeticiones; }
    public void setRepeticiones(int repeticiones) { this.repeticiones = repeticiones; }
    public String getPesoSugerido() { return pesoSugerido; }
    public void setPesoSugerido(String pesoSugerido) { this.pesoSugerido = pesoSugerido; }
    public Rutina getRutina() { return rutina; }
    public void setRutina(Rutina rutina) { this.rutina = rutina; }
}