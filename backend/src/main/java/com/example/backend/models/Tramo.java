package com.example.backend.models;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "Tramo")
public class Tramo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tramo")
    private Long id_tramo;

    @ManyToOne
    @JoinColumn(name = "fid_ubicacion_origen", nullable = false)
    private Ubicacion ubicacionOrigen;

    @ManyToOne
    @JoinColumn(name = "fid_ubicacion_destino", nullable = false)
    private Ubicacion ubicacionDestino;

    @ManyToOne
    @JoinColumn(name = "fid_plan_transporte")
    private PlanTransporte planTransporte;

    @Column(name = "bloqueado")
    private boolean bloqueado;

    @Column(name = "distancia")
    private float distancia;

    @Column(name = "velocidad")
    private float velocidad;

    @Column(name = "fechaInicio")
    private LocalDateTime fechaInicio;

    @Column(name = "fechaFin")
    private LocalDateTime fechaFin;

    @Column(name = "cantidadPaquetes")
    private int cantidadPaquetes;

    @ManyToOne
    @JoinColumn(name = "fid_vehiculo")
    private Vehiculo vehiculo;

    @Column(name = "duracion")
    private float duracion;

    @Column(name = "transitado")
    private boolean transitado;

    public Tramo() {
    }

    public Tramo( LocalDateTime fechaInicio, LocalDateTime fechaFin, Ubicacion ubicacionOrigen, Ubicacion ubicacionDestino, Vehiculo vehiculo, float duracion, boolean transitado, float velocidad, float distancia, boolean bloqueado) {
        this.fechaFin = fechaFin;
        this.fechaInicio = fechaInicio;
        this.ubicacionOrigen = ubicacionOrigen;
        this.ubicacionDestino = ubicacionDestino;
        this.vehiculo = vehiculo;
        this.duracion = duracion;
        this.transitado = transitado;
        this.velocidad = velocidad;
        this.distancia = distancia;
        this.bloqueado = bloqueado;
    }

    public Tramo(Ubicacion ubicacionOrigen, Ubicacion ubicacionDestino) {
        this.ubicacionOrigen = ubicacionOrigen;
        this.ubicacionDestino = ubicacionDestino;
    }

    public Tramo(Ubicacion ubicacionOrigen, Ubicacion ubicacionDestino, PlanTransporte planTransporte, boolean bloqueado, float distancia, float velocidad, LocalDateTime fechaInicio, LocalDateTime fechaFin, int cantidadPaquetes, Vehiculo vehiculo) {
        this.ubicacionOrigen = ubicacionOrigen;
        this.ubicacionDestino = ubicacionDestino;
        this.planTransporte = planTransporte;
        this.bloqueado = bloqueado;
        this.distancia = distancia;
        this.velocidad = velocidad;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.cantidadPaquetes = cantidadPaquetes;
        this.vehiculo = vehiculo;
    }

    public Tramo(Ubicacion ubicacionOrigen, Ubicacion ubicacionDestino, boolean bloqueado, float distancia, float velocidad, PlanTransporte planTransporte) {
        this.ubicacionOrigen = ubicacionOrigen;
        this.ubicacionDestino = ubicacionDestino;
        this.bloqueado = bloqueado;
        this.distancia = distancia;
        this.velocidad = velocidad;
        this.planTransporte = planTransporte;
    }

    public Long getId_tramo() {
        return id_tramo;
    }

    public Ubicacion getubicacionOrigen() {
        return ubicacionOrigen;
    }

    public void setubicacionOrigen(Ubicacion ubicacionOrigen) {
        this.ubicacionOrigen = ubicacionOrigen;
    }

    public Ubicacion getubicacionDestino() {
        return ubicacionDestino;
    }

    public void setubicacionDestino(Ubicacion ubicacionDestino) {
        this.ubicacionDestino = ubicacionDestino;
    }

    public boolean isBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    public float getDistancia() {
        return distancia;
    }

    public void setDistancia(float distancia) {
        this.distancia = distancia;
    }

    public float getVelocidad() {
        return velocidad;
    }

    public void setVelocidad(float velocidad) {
        this.velocidad = velocidad;
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

    public int getCantidadPaquetes() {
        return cantidadPaquetes;
    }

    public void setCantidadPaquetes(int cantidadPaquetes) {
        this.cantidadPaquetes = cantidadPaquetes;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    public float getDuracion() {
        return duracion;
    }

    public void setDuracion(float duracion) {
        this.duracion = duracion;
    }

    public PlanTransporte getPlanTransporte() {
        return planTransporte;
    }

    public void setPlanTransporte(PlanTransporte planTransporte) {
        this.planTransporte = planTransporte;
    }

    public boolean isTransitado() {
        return transitado;
    }

    public void setTransitado(boolean transitado) {
        this.transitado = transitado;
    }

    public Ubicacion getUbicacionOrigen() {
        return ubicacionOrigen;
    }

    public void setUbicacionOrigen(Ubicacion ubicacionOrigen) {
        this.ubicacionOrigen = ubicacionOrigen;
    }

    public Ubicacion getUbicacionDestino() {
        return ubicacionDestino;
    }

    public void setUbicacionDestino(Ubicacion ubicacionDestino) {
        this.ubicacionDestino = ubicacionDestino;
    }
}
