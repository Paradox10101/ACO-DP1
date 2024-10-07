
package com.example.backend.models;

import java.time.LocalDateTime;
import java.util.List;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;


@Entity
@Table(name = "PlanTransporte")
public class PlanTransporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_planTransporte")
    private Long id_planTransporte;

    @ManyToOne
    @JoinColumn(name = "fid_pedido")
    private Pedido pedido;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActalizacion;

    @ManyToOne
    @JoinColumn(name = "fid_ubicacion_origen")
    private Ubicacion ubicacionOrigen;

    @ManyToOne
    @JoinColumn(name = "fid_ubicacion_destino")
    private Ubicacion ubicacionDestino;



    @Column(name = "cantidad_transportada")
    private int cantidadTransportada;

    @ManyToOne
    @JoinColumn(name = "fid_vehiculo")
    private Vehiculo vehiculo;



    public PlanTransporte() {

    }

    public PlanTransporte(Long id_planTransporte, Pedido pedido, LocalDateTime fechaCreacion,
            LocalDateTime fechaActalizacion, Ubicacion ubicacionOrigen, Ubicacion ubicacionDestino) {
        this.id_planTransporte = id_planTransporte;
        this.pedido = pedido;
        this.fechaCreacion = fechaCreacion;
        this.fechaActalizacion = fechaActalizacion;
        this.ubicacionOrigen = ubicacionOrigen;
        this.ubicacionDestino = ubicacionDestino;
    }

    public Long getId_planTransporte() {
        return id_planTransporte;
    }

    public void setId_planTransporte(Long id_planTransporte) {
        this.id_planTransporte = id_planTransporte;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }


    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActalizacion() {
        return fechaActalizacion;
    }

    public void setFechaActalizacion(LocalDateTime fechaActalizacion) {
        this.fechaActalizacion = fechaActalizacion;
    }



    public int getCantidadTransportada() {
        return cantidadTransportada;
    }

    public Ubicacion getUbicacionDestino() {
        return ubicacionDestino;
    }

    public void setUbicacionDestino(Ubicacion ubicacionDestino) {
        this.ubicacionDestino = ubicacionDestino;
    }

    public Ubicacion getUbicacionOrigen() {
        return ubicacionOrigen;
    }

    public void setUbicacionOrigen(Ubicacion ubicacionOrigen) {
        this.ubicacionOrigen = ubicacionOrigen;
    }

    public void setCantidadTransportada(int cantidadTransportada) {
        this.cantidadTransportada = cantidadTransportada;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    private Oficina buscarOficinaPorId(Long idOficina, List<Oficina> oficinas) {
        for (Oficina oficina : oficinas) {
            if (oficina.getId_oficina().equals(idOficina)) {
                return oficina;
            }
        }
        return null; // Devuelve null si no encuentra la oficina
    }




}
