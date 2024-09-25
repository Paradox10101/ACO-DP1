
package com.example.backend.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Almacen")
public class Almacen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_almacen")
    private Long id_almacen;

    @Column(name = "fid_ubicacion")
    private Long fid_ubicacion;

    @Column(name = "cantidad_camiones")
    private int cantidadVehiculos;

    public Almacen(Long id_almacen, Long fid_ubicacion, int cantidadVehiculos) {
        this.id_almacen = id_almacen;
        this.fid_ubicacion = fid_ubicacion;
        this.cantidadVehiculos = cantidadVehiculos;
    }

    public Long getId_almacen() {
        return id_almacen;
    }

    public void setId_almacen(Long id_almacen) {
        this.id_almacen = id_almacen;
    }

    public Long getFid_ubicacion() {
        return fid_ubicacion;
    }

    public void setFid_ubicacion(Long fid_ubicacion) {
        this.fid_ubicacion = fid_ubicacion;
    }

    public int getCantidadVehiculos() {
        return cantidadVehiculos;
    }

    public void setCantidadVehiculos(int cantidadVehiculos) {
        this.cantidadVehiculos = cantidadVehiculos;
    }

    public Almacen() {
    }

}
