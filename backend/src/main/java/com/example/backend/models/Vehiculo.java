
package com.example.backend.models;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.*;

@Entity
@Table(name = "Vehiculo")
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vehiculo")
    private Long id_vehiculo;

    @Column(name = "codigo")
    private String codigo;

    @ManyToOne
    @JoinColumn(name = "fid_plan_transporte")
    private PlanTransporte planTransporte;

    @OneToOne
    @JoinColumn(name = "fid_almacen")
    private Almacen almacen;

    @ManyToOne
    @JoinColumn(name = "fid_ubicacion")
    private Ubicacion ubicacionActual;

    @OneToOne
    @JoinColumn(name = "fid_tipoVehiculo", nullable=false)
    private TipoVehiculo tipoVehiculo;

    @Column(name = "fecha_salida")
    private LocalDateTime fechaSalida;

    @Column(name = "fecha_llegada")
    private LocalDateTime fechaLlegada;

    @Column(name = "capacidad_maxima", nullable=false)
    private int capacidadMaxima;

    @Enumerated(EnumType.STRING)
    private EstadoVehiculo estado;
    

    @Column(name = "distancia_total")
    private float distanciaTotal;

    public Vehiculo(Long id_vehiculo, PlanTransporte planTransporte, Almacen almacen, LocalDateTime fechaSalida,
            LocalDateTime fechaLlegada, int capacidadMaxima, EstadoVehiculo estado, float distanciaTotal) {
        this.id_vehiculo = id_vehiculo;
        this.planTransporte = planTransporte;
        this.almacen = almacen;
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

    public PlanTransporte getPlanTransporte() {
        return planTransporte;
    }

    public void setPlanTransporte(PlanTransporte planTransporte) {
        this.planTransporte = planTransporte;
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

    public float getDistanciaTotal() {
        return distanciaTotal;
    }

    public void setDistanciaTotal(float distanciaTotal) {
        this.distanciaTotal = distanciaTotal;
    }


    public Almacen getAlmacen() {
        return almacen;
    }

    public void setAlmacen(Almacen almacen) {
        this.almacen = almacen;
    }

    public Ubicacion getUbicacionActual() {
        return ubicacionActual;
    }

    public void setFid_ubicacionActual(Ubicacion ubicacionActual) {
        this.ubicacionActual = ubicacionActual;
    }

    public TipoVehiculo getTipoVehiculo() {
        return tipoVehiculo;
    }

    public void setTipoVehiculo(TipoVehiculo tipoVehiculo) {
        this.tipoVehiculo = tipoVehiculo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
}
