package com.example.backend.models;

import java.sql.Time;
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
    private Date fechaProgramada;

    @Column(name = "duracion")
    private Time duracion;

    @ManyToOne
    @JoinColumn(name = "fid_vehiculo")
    private Vehiculo vehiculo;

    public Mantenimiento() {
    }

    public Mantenimiento(TipoMantenimiento tipo, Date fechaProgramada, Time duracion) {
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

    public Date getFechaProgramada() {
        return fechaProgramada;
    }

    public void setFechaProgramada(Date fechaProgramada) {
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
}
