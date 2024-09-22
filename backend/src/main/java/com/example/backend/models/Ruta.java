
package com.example.backend.models;

import java.time.LocalDateTime;
import java.util.ArrayList;

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
