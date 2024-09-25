
package com.example.backend.models;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "TipoVehiculo")
public class TipoVehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipoVehiculo")
    private Long id_tipoVehiculo;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "velocidad")
    private float velocidad;

    @Column(name = "capacidadMaxima")
    private int capacidadMaxima;

    @Column(name = "kilometraje_mantenimiento")
    private float kilometrajeMantenimiento;


    public TipoVehiculo() {
    }

    public TipoVehiculo(String nombre, float velocidad, int capacidadMaxima, float kilometrajeMantenimiento) {
        this.nombre = nombre;
        this.velocidad = velocidad;
        this.capacidadMaxima = capacidadMaxima;
        this.kilometrajeMantenimiento = kilometrajeMantenimiento;
    }

    public Long getId_tipoVehiculo() {
        return id_tipoVehiculo;
    }

    public void setId_tipoVehiculo(Long id_tipoVehiculo) {
        this.id_tipoVehiculo = id_tipoVehiculo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public float getVelocidad() {
        return velocidad;
    }

    public void setVelocidad(float velocidad) {
        this.velocidad = velocidad;
    }

    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public void setCapacidadMaxima(int capacidadMaxima) {
        this.capacidadMaxima = capacidadMaxima;
    }

    public float getKilometrajeMantenimiento() {
        return kilometrajeMantenimiento;
    }

    public void setKilometrajeMantenimiento(float kilometrajeMantenimiento) {
        this.kilometrajeMantenimiento = kilometrajeMantenimiento;
    }
}
