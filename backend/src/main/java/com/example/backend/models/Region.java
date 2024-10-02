
package com.example.backend.models;

import jakarta.persistence.*;

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

    @Column(name = "velocidad")
    private float velocidad=0;


    // Variable estática para manejar el autoincremento de IDs


    public Region() {

    }

    public Region(Region region) {
        this.id_region = region.id_region;
        this.nombre = region.nombre;
        this.diasLimite = region.diasLimite;
        this.velocidad = region.velocidad;
    }

    public Region(String nombre, int diasLimite) {

        this.nombre = nombre;
        this.diasLimite = diasLimite;
    }

    public Region(Long id_region, String nombre, int diasLimite) {

        this.nombre = nombre;
        this.diasLimite = diasLimite;
    }

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

    public float getVelocidad() {
        return velocidad;
    }

    public void setVelocidad(float velocidad) {
        
        this.velocidad = velocidad;
    }
    
}
