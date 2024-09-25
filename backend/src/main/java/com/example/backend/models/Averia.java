
package com.example.backend.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
@Table(name = "Averia")
public class Averia {

    @Id
    @Column(name = "id_almacen")
    private Long id_averia;

    @Column(name = "tipo")
    private TipoAveria tipo;

    @Column(name = "fecha_inicio")
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

    @OneToOne
    @JoinColumn(name = "fid_vehiculo")
    private Vehiculo vehiculo;
}
