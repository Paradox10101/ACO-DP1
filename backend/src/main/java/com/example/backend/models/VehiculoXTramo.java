
package com.example.backend.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "VehiculoXTramo")
public class VehiculoXTramo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_vehiculoXTramo;

    @Column(name = "fid_camion")
    private Long fid_camion;

    @Column(name = "fid_ruta")
    private Long fid_ruta;

    @Column(name = "distancia_recorrida")
    private float distanciaRecorrida;

    @Column(name = "capacidad_actual")
    private int capacidadActual;
}
