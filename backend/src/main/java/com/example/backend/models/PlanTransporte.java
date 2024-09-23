
package com.example.backend.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.GenerationType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
@Table(name = "PlanTransporte")
public class PlanTransporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_planTransporte")
    private Long id_planTransporte;

    @ManyToOne
    @JoinColumn(name = "id_pedido")
    private Long fid_pedido;

    @OneToMany(mappedBy = "planTransporte", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ArrayList<Vehiculo> vehiculos;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActalizacion;

    @ManyToOne
    @JoinColumn(name = "id_ubicacion")
    private Long fid_origen;

    @ManyToOne
    @JoinColumn(name = "id_ubicacion")
    private Long fid_destino;

    @OneToMany(mappedBy = "planTransporte", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ArrayList<Tramo> tramos;

}
