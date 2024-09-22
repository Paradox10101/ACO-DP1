
package com.example.backend.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="VehiculoXRuta")
public class VehiculoXRuta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_vehiculoXRuta;

    @Column(name="fid_camion")
    private int fid_camion;

    @Column(name="fid_ruta")
    private int fid_ruta;
    
    @Column(name="distancia_recorrida")
    private float distanciaRecorrida;
}
