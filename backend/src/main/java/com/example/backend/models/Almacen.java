
package com.example.backend.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;

@Entity
@Table(name = "Almacen")
public class Almacen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_almacen")
    private Long id_almacen;

    @OneToOne
    @JoinColumn(name = "fid_ubicacion", nullable=false)
    private Ubicacion ubicacion;

    @Column(name = "cantidad_vehiculos")
    private int cantidadVehiculos;


    public Almacen(){
       
    }

    public Almacen(Long id_almacen, Ubicacion ubicacion, int cantidadVehiculos) {
        this.id_almacen = id_almacen;
        this.ubicacion = ubicacion;
        this.cantidadVehiculos = cantidadVehiculos;
    }

    public Long getId_almacen() {
        return id_almacen;
    }

    public void setId_almacen(Long id_almacen) {
        this.id_almacen = id_almacen;
    }

    public Ubicacion getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(Ubicacion ubicacion) {
        this.ubicacion = ubicacion;
    }

    public int getCantidadVehiculos() {
        return cantidadVehiculos;
    }

    public void setCantidadVehiculos(int cantidadVehiculos) {
        this.cantidadVehiculos = cantidadVehiculos;
    }

}
