package com.example.backend.models;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "Tramo")
public class Tramo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tramo")
    private Long id_tramo;

    @ManyToOne
    @JoinColumn(name = "id_ubicacion_origen", nullable = false)
    private Long fid_ubicacion_origen;

    @ManyToOne
    @JoinColumn(name = "id_ubicacion_destino", nullable = false)
    private Long fid_ubicacion_destino;

    @Column(name = "bloqueado")
    private boolean bloqueado;

    @Column(name = "distancia")
    private float distancia;

    @Column(name = "velocidad")
    private float velocidad;

    @Column(name = "fechaInicio")
    private LocalDateTime fechaInicio;

    @Column(name = "fechaFin")
    private LocalDateTime fechaFin;

    @Column(name = "capacidadActual")
    private int capacidadActual;

    @ManyToOne
    @JoinColumn(name = "id_vehiculoXTramo", nullable = false)
    private Long fid_vehiculoXTramo;

    public Tramo(Long id_tramo,
            Long fid_ubicacion_origen,
            Long fid_ubicacion_destino, boolean bloqueado, float distancia,
            float velocidad) {
        this.id_tramo = id_tramo;
        this.fid_ubicacion_origen = fid_ubicacion_origen;
        this.fid_ubicacion_destino = fid_ubicacion_destino;
        this.bloqueado = bloqueado;
        this.distancia = distancia;
        this.velocidad = velocidad;
    }

    public Long getId_tramo() {
        return id_tramo;
    }

    public void setId_tramo(Long id_tramo) {
        this.id_tramo = id_tramo;
    }

    public Long getFid_ubicacion_origen() {
        return fid_ubicacion_origen;
    }

    public void setFid_ubicacion_origen(Long fid_ubicacion_origen) {
        this.fid_ubicacion_origen = fid_ubicacion_origen;
    }

    public Long getFid_ubicacion_destino() {
        return fid_ubicacion_destino;
    }

    public void setFid_ubicacion_destino(Long fid_ubicacion_destino) {
        this.fid_ubicacion_destino = fid_ubicacion_destino;
    }

    public boolean isBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    public float getDistancia() {
        return distancia;
    }

    public void setDistancia(float distancia) {
        this.distancia = distancia;
    }

    public float getVelocidad() {
        return velocidad;
    }

    public void setVelocidad(float velocidad) {
        this.velocidad = velocidad;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setVelocidad(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public int getCapacidadActual() {
        return capacidadActual;
    }

    public void setCapacidadActual(int capacidadActual) {
        this.capacidadActual = capacidadActual;
    }

    public static ArrayList<Tramo> cargarTramosDesdeArchivo(String rutaArchivo) {
        ArrayList<Tramo> tramos = new ArrayList<>();
        try {
            Path path = Paths.get(rutaArchivo).toAbsolutePath();
            try (BufferedReader br = Files.newBufferedReader(path)) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    String[] valores = linea.split(" => ");
                    String ubigeoOrigen = valores[0];
                    String ubigeoDestino = valores[1];

                    System.out.println(linea);
                }
            }

        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
        return tramos;
    }
}
