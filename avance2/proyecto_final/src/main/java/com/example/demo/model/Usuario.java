package com.example.demo.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

@Entity
@Table(name = "usuarios")
public class Usuario {
    
    @Id
    @Column(length = 100)
    private String email;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false, length = 100)
    private String nombre;
    @NotBlank(message = "El objetivo es obligatorio")
    @Column(length = 50)
    private String objetivo;
    
    // VALIDACIONES EXIGIDAS POR EL SILABO
    @NotNull(message = "El peso es obligatorio")
    @DecimalMin(value = "30.0", message = "Ingrese un peso válido (mínimo 30 kg)")
    private Double pesoActual;
    
    @NotNull(message = "La altura es obligatoria")
    @DecimalMin(value = "1.0", message = "Ingrese una altura válida (mínimo 1.0 m)")
    private Double altura;
    
    @NotNull(message = "Las calorías meta son obligatorias")
    @Min(value = 1200, message = "La meta calórica debe ser de al menos 1200 kcal")
    private Integer metaCalorias;
    
    @Column(length = 50)
    private String nivelEntrenamiento;
    
    @Column(length = 50)
    private String membresia; 
    
    @Column(nullable = false)
    private String password;

    private Integer edad;
    
    @Column(length = 20)
    private String genero;
    
    @Past(message = "La fecha de nacimiento debe ser anterior a hoy")
    private LocalDate fechaNacimiento;
    
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean suspendido = false;

    @Column(length = 20)
    private String rol;

    public Usuario() {
    }

    public Usuario(String email, String nombre, Double pesoActual, Double altura, Integer metaCalorias, 
                   String nivelEntrenamiento, String membresia, String password, Integer edad, String genero, LocalDate fechaNacimiento, String objetivo, String rol) {
        this.email = email;
        this.nombre = nombre;
        this.pesoActual = pesoActual;
        this.altura = altura;
        this.metaCalorias = metaCalorias;
        this.nivelEntrenamiento = nivelEntrenamiento;
        this.membresia = membresia;
        this.password = password;
        this.edad = edad;
        this.genero = genero;
        this.fechaNacimiento = fechaNacimiento;
        this.objetivo = objetivo;
        this.rol = rol;
    }
    @AssertTrue(message = "Ingresa una fecha de nacimiento válida (la edad debe estar entre 13 y 100 años)")
        public boolean isEdadValida() {
    if (fechaNacimiento == null) {
        return false;
}
    int edadCalculada = java.time.Period.between(fechaNacimiento, LocalDate.now()).getYears();

    return edadCalculada >= 13 && edadCalculada <= 100;
}
    // Getters y Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Double getPesoActual() { return pesoActual; }
    public void setPesoActual(Double pesoActual) { this.pesoActual = pesoActual; }

    public Double getAltura() { return altura; }
    public void setAltura(Double altura) { this.altura = altura; }

    public Integer getMetaCalorias() { return metaCalorias; }
    public void setMetaCalorias(Integer metaCalorias) { this.metaCalorias = metaCalorias; }

    public String getNivelEntrenamiento() { return nivelEntrenamiento; }
    public void setNivelEntrenamiento(String nivelEntrenamiento) { this.nivelEntrenamiento = nivelEntrenamiento; }

    public String getMembresia() { return membresia; }
    public void setMembresia(String membresia) { this.membresia = membresia; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Integer getEdad() { return edad; }
    public void setEdad(Integer edad) { this.edad = edad; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    
    public String getObjetivo() { return objetivo; }
    public void setObjetivo(String objetivo) { this.objetivo = objetivo; }
    
    public boolean isSuspendido() { return suspendido; }
    public void setSuspendido(boolean suspendido) { this.suspendido = suspendido; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}