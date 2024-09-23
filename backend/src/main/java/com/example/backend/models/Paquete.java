
package com.example.backend.models;

import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Table;

@Entity
@Table(name = "Paquete")
public class Paquete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_paquete")
    private Long id_paquete;

    @Column(name = "fid_almacen")
    private Long fid_almacen;

    @Column(name = "fid_pedido")
    private Long fid_pedido;

    @Column(name = "fid_tramoActual")
    private Long fid_tramoActual;

    @Enumerated(EnumType.STRING)
    private EstadoPaquete estado;

    public Paquete(Long id_paquete, Long fid_almacen, Long fid_pedido, Long fid_tramoActual, EstadoPaquete estado) {
        this.id_paquete = id_paquete;
        this.fid_almacen = fid_almacen;
        this.fid_pedido = fid_pedido;
        this.fid_tramoActual = fid_tramoActual;
        this.estado = estado;
    }
}
