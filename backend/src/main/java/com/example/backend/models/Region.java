
package com.example.backend.models;

import jakarta.persistence.*;

import java.util.ArrayList;

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

    @Transient
    private ArrayList<Region>relacionRegiones = new ArrayList<>();


    public Region() {

    }

    public Region(String nombre, int diasLimite) {
        this.nombre = nombre;
        this.diasLimite = diasLimite;
    }

    public Region(Long id_region, String nombre, int diasLimite) {
        this.id_region = id_region;
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

    public ArrayList<Region> getRelacionRegiones() {
        return relacionRegiones;
    }

    public void setRelacionRegionVelocidad(Region region, float velocidad) {
        region.setVelocidad(velocidad);
        this.relacionRegiones.add(region);
    }

    
    
}
