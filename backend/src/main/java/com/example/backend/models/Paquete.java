
package com.example.backend.models;

import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Entity
@Table(name = "Paquete")
public class Paquete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_paquete")
    private Long id_paquete;

    @ManyToOne
    @JoinColumn(name = "fid_almacen")
    private Almacen almacen;

    @ManyToOne
    @JoinColumn(name = "fid_pedido")
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "fid_tramoActual")
    private Tramo tramoActual;

    @Enumerated(EnumType.STRING)
    private EstadoPaquete estado;
    

    public Paquete(Long id_paquete, Almacen almacen, Pedido pedido, Tramo tramoActual, EstadoPaquete estado) {
        this.almacen = almacen;
        this.pedido = pedido;
        this.tramoActual = tramoActual;
        this.estado = estado;
    }

    public Paquete()
    {
    }
    
    public Almacen getAlmacen() {
        return almacen;
    }

    public void setAlmacen(Almacen almacen) {
        this.almacen = almacen;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Tramo getTramoActual() {
        return tramoActual;
    }

    public void setTramoActual(Tramo tramoActual) {
        this.tramoActual = tramoActual;
    }

    public EstadoPaquete getEstado() {
        return estado;
    }

    public void setEstado(EstadoPaquete estado) {
        this.estado = estado;
    }

}
