package com.example.backend.models;

import java.time.LocalDateTime;

import jakarta.persistence.*;

//@AllArgsConstructor
//@NoArgsConstructor
/*@Getter
@Setter*/
//@Component
@Entity
@Table(name = "Bloqueo")
public class Bloqueo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_bloqueo")
    private Long id_bloqueo;

    @Column(name = "fecha_inicio")
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

    @OneToOne
    @JoinColumn(name = "fid_tramoAfectado")
    private Tramo tramoAfectado;

    @ManyToOne
    @JoinColumn(name = "fid_ubicacionOrigen", nullable = false)
    private Ubicacion ubicacionOrigen;

    @ManyToOne
    @JoinColumn(name = "fid_ubicacionDestino", nullable = false)
    private Ubicacion ubicacionDestino;

    public Bloqueo() {

    }

    public Bloqueo(LocalDateTime fechaInicio, LocalDateTime fechaFin, Tramo tramoAfectado) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.tramoAfectado = tramoAfectado;
    }

    public Bloqueo(LocalDateTime fechaInicio, LocalDateTime fechaFin, Ubicacion ubicacionOrigen, Ubicacion ubicacionDestino) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.ubicacionOrigen = ubicacionOrigen;
        this.ubicacionDestino = ubicacionDestino;
    }

    public Long getId_bloqueo() {
        return id_bloqueo;
    }

    public void setId_bloqueo(Long id_bloqueo) {
        this.id_bloqueo = id_bloqueo;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Tramo getTramoAfectado() {
        return tramoAfectado;
    }

    public void setTramoAfectado(Tramo tramoAfectado) {
        this.tramoAfectado = tramoAfectado;
    }


    public Ubicacion getUbicacionOrigen() {
        return ubicacionOrigen;
    }

    public void setUbicacionOrigen(Ubicacion ubicacionOrigen) {
        this.ubicacionOrigen = ubicacionOrigen;
    }

    public Ubicacion getUbicacionDestino() {
        return ubicacionDestino;
    }

    public void setUbicacionDestino(Ubicacion ubicacionDestino) {
        this.ubicacionDestino = ubicacionDestino;
    }
}

