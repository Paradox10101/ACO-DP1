
package com.example.backend.models;

import java.time.LocalDateTime;

public class Bloqueo {
    private Long id_bloqueo;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Tramo tramoAfectado;
}
