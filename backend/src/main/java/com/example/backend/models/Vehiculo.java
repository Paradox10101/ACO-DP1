
package com.example.backend.models;

import java.time.LocalDateTime;
import java.util.ArrayList;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "Vehiculo")
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vehiculo")
    private Long id_vehiculo;

    @ManyToMany
    @Column(name = "id_plan_transporte")
    private Long fid_plan_transporte;

    @ManyToOne
    @JoinColumn(name = "id_almacen")
    private int fid_Almacen;

    @Column(name = "fecha_salida")
    private LocalDateTime fechaSalida;

    @Column(name = "fecha_llegada")
    private LocalDateTime fechaLlegada;

    @Column(name = "capacidad_maxima")
    private int capacidadMaxima;

    @Column(name = "estado")
    private EstadoVehiculo estado;

    private ArrayList<Mantenimiento> mantenimientos;

    private ArrayList<Averia> averias;

    @Column(name = "distancia_total")
    private float distanciaTotal;

    public Vehiculo(Long id_vehiculo, Long fid_plan_transporte, int fidAlmacen, LocalDateTime fechaSalida,
            LocalDateTime fechaLlegada, int capacidadMaxima, EstadoVehiculo estado, float distanciaTotal) {
        this.id_vehiculo = id_vehiculo;
        this.fid_plan_transporte = fid_plan_transporte;
        this.fid_Almacen = fidAlmacen;
        this.fechaSalida = fechaSalida;
        this.fechaLlegada = fechaLlegada;
        this.capacidadMaxima = capacidadMaxima;
        this.estado = estado;
        this.distanciaTotal = distanciaTotal;
    }

    public Vehiculo() {

    }

    public Long getId_vehiculo() {
        return id_vehiculo;
    }

    public void setId_vehiculo(Long id_vehiculo) {
        this.id_vehiculo = id_vehiculo;
    }

    public Long getFid_plan_transporte() {
        return fid_plan_transporte;
    }

    public void setFid_plan_transporte(Long id_plan_transporte) {
        this.fid_plan_transporte = id_plan_transporte;
    }

    public int getFidAlmacen() {
        return fid_Almacen;
    }

    public void setFid_Almacen(int fid_Almacen) {
        this.fid_Almacen = fid_Almacen;
    }

    public LocalDateTime getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(LocalDateTime fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public LocalDateTime getFechaLlegada() {
        return fechaLlegada;
    }

    public void setFechaLlegada(LocalDateTime fechaLlegada) {
        this.fechaLlegada = fechaLlegada;
    }

    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public void setCapacidadMaxima(int capacidadMaxima) {
        this.capacidadMaxima = capacidadMaxima;
    }

    public EstadoVehiculo getEstado() {
        return estado;
    }

    public void setEstado(EstadoVehiculo estado) {
        this.estado = estado;
    }

    public ArrayList<Mantenimiento> getMantenimientos() {
        return mantenimientos;
    }

    public void setMantenimientos(ArrayList<Mantenimiento> mantenimientos) {
        this.mantenimientos = mantenimientos;
    }

    public ArrayList<Averia> getAverias() {
        return averias;
    }

    public void setAverias(ArrayList<Averia> averias) {
        this.averias = averias;
    }

    public float getDistanciaTotal() {
        return distanciaTotal;
    }

    public void setDistanciaTotal(float distanciaTotal) {
        this.distanciaTotal = distanciaTotal;
    }


}
