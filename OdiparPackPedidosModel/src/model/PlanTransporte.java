/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 *
 * @author Yahyr
 */
public class PlanTransporte {
    private int id_planTransporte;
    private ArrayList<Vehiculo> vehiculos;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActalizacion;
    private Ubicacion origen;
    private Ubicacion destino;
}
