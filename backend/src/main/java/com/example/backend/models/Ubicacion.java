
package com.example.backend.models;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Entity;

@Entity
public class Ubicacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_ubicacion")
    private int id_ubicacion;

    @Column(name="coordenada")
    private String coordenada;

    @Column(name="ciudad")
    private String ciudad;

    @ManyToOne
    @JoinColumn(name="id_region")
    private Region region;
}
