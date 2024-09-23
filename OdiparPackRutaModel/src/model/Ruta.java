
package com.example.backend.models;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Ruta {
    private long id_ruta;
    private long fid_camion;
    private float distanciaTotal;
    private Ubicacion origen;
    private Ubicacion destino;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private ArrayList<Tramo> tramos;
}
