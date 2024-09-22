
package com.example.backend.models;

import java.time.LocalDateTime;
import java.util.ArrayList;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name="Vehiculo")
public class Vehiculo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_vehiculo")
    private int id_vehiculo;

    @Column(name="id_plan_transporte")
    private int id_plan_transporte;

    @ManyToOne
    @JoinColumn(name="id_almacen")
    private Almacen almacen;
    
    @Column(name="fecha_salida")
    private LocalDateTime fechaSalida;

    @Column(name="fecha_llegada")
    private LocalDateTime fechaLlegada;
    
    @Column(name="capacidad_actual")
    private int capacidadActual;
    
    @Column(name="capacidad_maxima")
    private int capacidadMaxima;
    
    @Column(name="estado")
    private EstadoVehiculo estado;

    
    private ArrayList<Mantenimiento> mantenimientos;
    
    
    private ArrayList<Averia> averias;
    
    @Column(name="distancia_total")
    private float distanciaTotal;

}
