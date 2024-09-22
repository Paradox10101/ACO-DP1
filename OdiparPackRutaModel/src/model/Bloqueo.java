
package com.example.backend.models;

import java.time.LocalDateTime;

public class Bloqueo {
    private int id_bloqueo;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Tramo tramoAfectado;
}
