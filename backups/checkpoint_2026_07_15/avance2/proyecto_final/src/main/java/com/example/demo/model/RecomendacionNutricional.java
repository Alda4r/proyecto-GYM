package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "entrenador_alimentos")
public class RecomendacionNutricional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private int calorias;
    
    @Column(name = "tipo_comida")
    private String tipoComida;


    @Column(name = "usuario_email")
    private String usuarioEmail;

    public RecomendacionNutricional() {
    }

    public RecomendacionNutricional(String nombre, int calorias, String tipoComida, String usuarioEmail) {
        this.nombre = nombre;
        this.calorias = calorias;
        this.tipoComida = tipoComida;
        this.usuarioEmail = usuarioEmail;
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public int getCalorias() { return calorias; }
    public void setCalorias(int calorias) { this.calorias = calorias; }
    
    public String getTipoComida() { return tipoComida; }
    public void setTipoComida(String tipoComida) { this.tipoComida = tipoComida; }
    
    public String getUsuarioEmail() { return usuarioEmail; }
    public void setUsuarioEmail(String usuarioEmail) { this.usuarioEmail = usuarioEmail; }
}