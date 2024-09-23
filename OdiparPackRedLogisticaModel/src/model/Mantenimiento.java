
package com.example.backend.models;

import java.sql.Time;
import java.time.LocalDateTime;

public class Mantenimiento {
    private Long id_mantenimiento;
    private TipoMantenimiento tipo;
    private LocalDateTime fechaProgramada;
    private Time duracion;
}
