package com.example.backend.models;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public Mantenimiento(Long id_mantenimiento, TipoMantenimiento tipo, Date fechaProgramada, Time duracion) {
        this.id_mantenimiento = id_mantenimiento;
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
