
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


    public Simulacion() {
    }

    public Simulacion(Long id_simulacion, TipoSimulacion tipo, LocalDateTime fechaInicioRealSimulacion, LocalDateTime fechaInicioSimulacion, LocalDateTime fechaFinSimulacion, EstadoSimulacion estado, VelocidadSimulacion velocidad) {
        this.id_simulacion = id_simulacion;
        this.tipo = tipo;
        this.fechaInicioRealSimulacion = fechaInicioRealSimulacion;
        this.fechaInicioSimulacion = fechaInicioSimulacion;
        this.fechaFinSimulacion = fechaFinSimulacion;
        this.estado = estado;
        this.velocidad = velocidad;
    }

    public Long getId_simulacion() {
        return id_simulacion;
    }

    public void setId_simulacion(Long id_simulacion) {
        this.id_simulacion = id_simulacion;
    }

    public TipoSimulacion getTipo() {
        return tipo;
    }

    public void setTipo(TipoSimulacion tipo) {
        this.tipo = tipo;
    }

    public LocalDateTime getFechaInicioRealSimulacion() {
        return fechaInicioRealSimulacion;
    }

    public void setFechaInicioRealSimulacion(LocalDateTime fechaInicioRealSimulacion) {
        this.fechaInicioRealSimulacion = fechaInicioRealSimulacion;
    }

    public LocalDateTime getFechaInicioSimulacion() {
        return fechaInicioSimulacion;
    }

    public void setFechaInicioSimulacion(LocalDateTime fechaInicioSimulacion) {
        this.fechaInicioSimulacion = fechaInicioSimulacion;
    }

    public LocalDateTime getFechaFinSimulacion() {
        return fechaFinSimulacion;
    }

    public void setFechaFinSimulacion(LocalDateTime fechaFinSimulacion) {
        this.fechaFinSimulacion = fechaFinSimulacion;
    }

    public EstadoSimulacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoSimulacion estado) {
        this.estado = estado;
    }

    public VelocidadSimulacion getVelocidad() {
        return velocidad;
    }

    public void setVelocidad(VelocidadSimulacion velocidad) {
        this.velocidad = velocidad;
    }
}
