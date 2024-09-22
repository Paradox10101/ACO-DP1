
package com.example.backend.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="Almacen")
public class Almacen {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_almacen")
    private int id_almacen;

    @Column(name = "fid_ubicacion")
    private int fid_ubicacion;

    @Column(name = "cantidad_camiones")
    private int cantidadCamiones;

    public Almacen(int id_almacen, int fid_ubicacion, int cantidadCamiones) {
        this.id_almacen = id_almacen;
        this.fid_ubicacion = fid_ubicacion;
        this.cantidadCamiones = cantidadCamiones;
    }

    public int getId_almacen() {
        return id_almacen;
    }

    public void setId_almacen(int id_almacen) {
        this.id_almacen = id_almacen;
    }

    public int getFid_ubicacion() {
        return fid_ubicacion;
    }

    public void setFid_ubicacion(int fid_ubicacion) {
        this.fid_ubicacion = fid_ubicacion;
    }

    public int getCantidadCamiones() {
        return cantidadCamiones;
    }

    public void setCantidadCamiones(int cantidadCamiones) {
        this.cantidadCamiones = cantidadCamiones;
    }

    public Almacen() {
    }

}
