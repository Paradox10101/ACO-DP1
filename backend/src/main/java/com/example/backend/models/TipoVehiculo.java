
package com.example.backend.models;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "TipoVehiculo")
public class TipoVehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipoVehiculo")
    private long id_tipoVehiculo;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "velocidad")
    private float velocidad;

    @Column(name = "kilometraje_mantenimiento")
    private float kilometrajeMantenimiento;
}
