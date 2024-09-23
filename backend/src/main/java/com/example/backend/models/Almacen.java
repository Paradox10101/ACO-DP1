
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
    private long id_almacen;

    @Column(name = "fid_ubicacion")
    private long fid_ubicacion;

    @Column(name = "cantidad_camiones")
    private int cantidadCamiones;

    public Almacen(long id_almacen, long fid_ubicacion, int cantidadCamiones) {
        this.id_almacen = id_almacen;
        this.fid_ubicacion = fid_ubicacion;
        this.cantidadCamiones = cantidadCamiones;
    }

    public long getId_almacen() {
        return id_almacen;
    }

    public void setId_almacen(long id_almacen) {
        this.id_almacen = id_almacen;
    }

    public long getFid_ubicacion() {
        return fid_ubicacion;
    }

    public void setFid_ubicacion(long fid_ubicacion) {
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
