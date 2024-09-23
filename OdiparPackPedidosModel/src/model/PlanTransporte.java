
package com.example.backend.models;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class PlanTransporte {
    private Long id_planTransporte;
    private ArrayList<Vehiculo> vehiculos;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActalizacion;
    private Ubicacion origen;
    private Ubicacion destino;
}
