package com.example.backend.models;

import java.time.LocalDateTime;
import java.util.ArrayList;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "Ruta")
public class Ruta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ruta")
    private Long id_ruta;

    @ManyToOne
    @JoinColumn(name = "id_vehiculo", nullable = false)
    private Long fid_vehiculo;

    @Column(name = "distancia_total")
    private float distanciaTotal;

    @ManyToOne
    @JoinColumn(name = "id_ubicacion", nullable = false)
    private Long fid_ubicacion_origen;

    @ManyToOne
    @JoinColumn(name = "id_ubicacion", nullable = false)
    private Long fid_ubicacion_destino;

    @Column(name = "fechaInicio", columnDefinition = "DATETIME")
    private LocalDateTime fechaInicio;

    @Column(name = "fechaFin", columnDefinition = "DATETIME")
    private LocalDateTime fechaFin;

    @OneToMany(mappedBy = "ruta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ArrayList<Tramo> tramos;

    // Variable estática para manejar el autoincremento de IDs
    private static Long idCounter = 1L;

    public Ruta() {
        this.id_ruta = idCounter++;
    }

    public Ruta(
            Long id_ruta, Long fid_vehiculo, float distanciaTotal,
            Long id_ubicacion_origen,
            Long id_ubicacion_destino,
            LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        this.id_ruta = idCounter++;
        this.fid_vehiculo = fid_vehiculo;
        this.distanciaTotal = distanciaTotal;
        this.fid_ubicacion_origen = id_ubicacion_origen;
        this.fid_ubicacion_destino = id_ubicacion_destino;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public Long getId_ruta() {
        return id_ruta;
    }

    public void setId_ruta(Long id_ruta) {
        this.id_ruta = id_ruta;
    }

    public Long getFid_vehiculo() {
        return fid_vehiculo;
    }

    public void setFid_vehiculo(Long fid_vehiculo) {
        this.fid_vehiculo = fid_vehiculo;
    }

    public float getDistanciaTotal() {
        return distanciaTotal;
    }

    public void setDistanciaTotal(float distanciaTotal) {
        this.distanciaTotal = distanciaTotal;
    }

    public Long getOrigen() {
        return fid_ubicacion_origen;
    }

    public void setOrigen(Long fid_ubicacion_origen) {
        this.fid_ubicacion_origen = fid_ubicacion_origen;
    }

    public Long getDestino() {
        return fid_ubicacion_destino;
    }

    public void setDestino(Long fid_ubicacion_destino) {
        this.fid_ubicacion_destino = fid_ubicacion_destino;
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

}
