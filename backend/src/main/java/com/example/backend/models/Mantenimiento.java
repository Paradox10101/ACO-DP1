package com.example.backend.models;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import jakarta.persistence.*;

@Entity
@Table(name = "Mantenimiento")
public class Mantenimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mantenimiento")
    private Long id_mantenimiento;
    
    @Column(name = "tipo")
    private TipoMantenimiento tipo;

    @Column(name = "fechaProgramada")
    private LocalDate fechaProgramada;

    @Column(name = "duracion")
    private Time duracion;

    @Column(name = "fechaInicio")
    private LocalDateTime fechaInicio;

    @Column(name = "fechaFin")
    private LocalDateTime fechaFin;

    @ManyToOne
    @JoinColumn(name = "fid_vehiculo")
    private Vehiculo vehiculo;

    @Column(name = "pendiente")
    private boolean pendiente;

    public Mantenimiento() {
    }

    public Mantenimiento(TipoMantenimiento tipo, LocalDate fechaProgramada, Time duracion) {
        this.tipo = tipo;
        this.fechaProgramada = fechaProgramada;
        this.duracion = duracion;
    }

    public Long getId_mantenimiento() {
        return id_mantenimiento;
    }

    public void setId_mantenimiento(Long id_mantenimiento) {
        this.id_mantenimiento = id_mantenimiento;
    }

    public TipoMantenimiento getTipo() {
        return tipo;
    }

    public void setTipo(TipoMantenimiento tipo) {
        this.tipo = tipo;
    }

    public LocalDate getFechaProgramada() {
        return fechaProgramada;
    }

    public void setFechaProgramada(LocalDate fechaProgramada) {
        this.fechaProgramada = fechaProgramada;
    }

    public Time getDuracion() {
        return duracion;
    }

    public void setDuracion(Time duracion) {
        this.duracion = duracion;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
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

    public boolean isPendiente() {
        return pendiente;
    }

    public void setPendiente(boolean pendiente) {
        this.pendiente = pendiente;
    }
}
