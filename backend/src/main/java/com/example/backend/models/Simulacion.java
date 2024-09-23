
package com.example.backend.models;

import java.time.LocalDateTime;
import jakarta.persistence.Table;

@Table(name = "Simulacion")
public class Simulacion {
    private Long id_simulacion;
    private TipoSimulacion tipo;
    private LocalDateTime fechaInicioRealSimulacion;
    private LocalDateTime fechaInicioSimulacion;
    private LocalDateTime fechaFinSimulacion;
    private EstadoSimulacion estado;
    private VelocidadSimulacion velocidad;

}
