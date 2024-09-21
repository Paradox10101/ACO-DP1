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
public class Ruta {
    private int id_ruta;
    private int fid_camion;
    private float distanciaTotal;
    private Ubicacion origen;
    private Ubicacion destino;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private ArrayList<Tramo> tramos;
}
