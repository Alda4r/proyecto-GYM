package com.example.demo.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "comidas_registro") // Asegúrate de que coincida con tu tabla de SQL Server
public class Comida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_comida")
    private String tipoComida;

    @Column(name = "nombre_alimento")
    private String nombreAlimento;

    private int calorias;
    
    // 🛠️ VERIFICA QUE ESTOS ATRIBUTOS ESTÉN ASÍ:
    private int proteina;
    private int carbohidratos;
    private int grasas;

    private LocalDate fecha;

    @Column(name = "usuario_email")
    private String usuarioEmail;

    // Constructor vacío obligatorio para JPA
    public Comida() {
    }

    // ========== GETTERS Y SETTERS ==========
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTipoComida() { return tipoComida; }
    public void setTipoComida(String tipoComida) { this.tipoComida = tipoComida; }

    public String getNombreAlimento() { return nombreAlimento; }
    public void setNombreAlimento(String nombreAlimento) { this.nombreAlimento = nombreAlimento; }

    public int getCalorias() { return calorias; }
    public void setCalorias(int calorias) { this.calorias = calorias; }

    // 🛠️ ESTOS SON LOS MÉTODOS QUE TE FALTABAN PARA REPARAR EL ERROR:
    public int getProteina() { return proteina; }
    public void setProteina(int proteina) { this.proteina = proteina; }

    public int getCarbohidratos() { return carbohidratos; }
    public void setCarbohidratos(int carbohidratos) { this.carbohidratos = carbohidratos; }

    public int getGrasas() { return grasas; }
    public void setGrasas(int grasas) { this.grasas = grasas; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getUsuarioEmail() { return usuarioEmail; }
    public void setUsuarioEmail(String usuarioEmail) { this.usuarioEmail = usuarioEmail; }
}