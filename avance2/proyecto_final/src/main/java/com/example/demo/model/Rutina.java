package com.example.demo.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "rutinas")
public class Rutina {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;

    @Column(nullable = false, length = 100, unique = true) 
    private String nombre;

    @Column(length = 50)
    private String nivel;

    @Column(length = 30)
    private String tiempo;

    @Column(length = 50, name = "grupo_muscular")
    private String grupoMuscular; 

    @Column(name = "objetivo_asociado", length = 50, nullable = false)
    private String objetivoAsociado;
    
    @Column(name = "usuario_email")
    private String usuarioEmail;

    @OneToMany(mappedBy = "rutina", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Ejercicio> listaEjercicios;

    public Rutina() {
    }

    public Rutina(String nombre, String nivel, String tiempo, String grupoMuscular, String objetivoAsociado, List<Ejercicio> listaEjercicios, String usuarioEmail) {
        this.nombre = nombre;
        this.nivel = nivel;
        this.tiempo = tiempo;
        this.grupoMuscular = grupoMuscular;
        this.objetivoAsociado = objetivoAsociado;
        this.listaEjercicios = listaEjercicios;
        this.usuarioEmail = usuarioEmail;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }

    public String getTiempo() { return tiempo; }
    public void setTiempo(String tiempo) { this.tiempo = tiempo; }

    public String getGrupoMuscular() { return grupoMuscular; }
    public void setGrupoMuscular(String grupoMuscular) { this.grupoMuscular = grupoMuscular; }

    // GETTER Y SETTER AÑADIDOS PARA EL FILTRADO INTELIGENTE
    public String getObjetivoAsociado() { return objetivoAsociado; }
    public void setObjetivoAsociado(String objetivoAsociado) { this.objetivoAsociado = objetivoAsociado; }

    public List<Ejercicio> getListaEjercicios() { return listaEjercicios; }
    public void setListaEjercicios(List<Ejercicio> listaEjercicios) { this.listaEjercicios = listaEjercicios; }
    public String getUsuarioEmail() { return usuarioEmail; }
    public void setUsuarioEmail(String usuarioEmail) { this.usuarioEmail = usuarioEmail; }
}