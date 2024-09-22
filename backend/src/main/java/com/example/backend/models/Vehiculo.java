
package com.example.backend.models;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Vehiculo {
    private int id_vehiculo;
    private int id_plan_transporte;
    private Almacen almacen;
    private LocalDateTime fechaSalida;
    private LocalDateTime fechaLlegada;
    private int capacidadActual;
    private int capacidadMaxima;
    private EstadoVehiculo estado;
    private ArrayList<Mantenimiento> mantenimientos;
    private ArrayList<Averia> averias;
    private float distanciaTotal;

}
