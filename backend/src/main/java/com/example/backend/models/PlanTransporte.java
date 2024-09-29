
package com.example.backend.models;

import java.time.LocalDateTime;


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

    @OneToOne
    @JoinColumn(name = "fid_ubicacion_origen")
    private Ubicacion ubicacionOrigen;

    @OneToOne
    @JoinColumn(name = "fid_ubicacion_destino")
    private Ubicacion ubicacionDestino;

    @Enumerated(EnumType.STRING)
    private EstadoPedido estado;

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

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }
}
