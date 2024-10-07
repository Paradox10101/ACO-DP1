
package com.example.backend.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

@Entity
@Table(name = "Averia")
public class Averia {

    @Id
    @Column(name = "id_almacen")
    private Long id_averia;

    @Column(name = "tipo")
    private TipoAveria tipo;

    @Column(name = "fecha_inicio")
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

    @ManyToOne
    @JoinColumn(name = "fid_vehiculo", nullable = false)
    private Vehiculo vehiculo;

    @Column(name = "descripcion")
    private String descripcion;

    public Averia() {
    }

    public Averia(TipoAveria tipo, LocalDateTime fechaInicio, LocalDateTime fechaFin, Vehiculo vehiculo) {
        this.tipo = tipo;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.vehiculo = vehiculo;
    }

    public TipoAveria getTipo() {
        return tipo;
    }

    public void setTipo(TipoAveria tipo) {
        this.tipo = tipo;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    
}
