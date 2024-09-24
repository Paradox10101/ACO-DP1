
package com.example.backend.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Region")
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_region")
    private Long id_region;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "dias_limite")
    private int diasLimite;

    public Long getIdRegion() {
        return id_region;
    }

    public void setIdRegion(Long id_region) {
        this.id_region = id_region;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getDiasLimite() {
        return diasLimite;
    }

    public void setDiasLimite(int diasLimite) {
        this.diasLimite = diasLimite;
    }

    public Region() {

    }

    public Region(Long id_region, String nombre, int diasLimite) {
        this.id_region = id_region;
        this.nombre = nombre;
        this.diasLimite = diasLimite;
    }
    
}
