
package com.example.backend.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.GenerationType;

@Entity
@Table(name="PlanTransporte")
public class PlanTransporte {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_planTransporte")
    private int id_planTransporte;
    
    
    private ArrayList<Vehiculo> vehiculos;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActalizacion;
    private Ubicacion origen;
    private Ubicacion destino;
}
