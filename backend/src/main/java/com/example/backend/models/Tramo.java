package com.example.backend.models;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

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
    @Column(name = "id_ubicacion")
    private long id_tramo;

    @ManyToOne
    @JoinColumn(name = "id_ubicacion_origen", nullable = false)
    private long fid_ubicacion_origen;

    @ManyToOne
    @JoinColumn(name = "id_ubicacion_destino", nullable = false)
    private long fid_ubicacion_destino;

    @Column(name = "bloqueado")
    private boolean bloqueado;

    @Column(name = "distancia")
    private float distancia;

    @Column(name = "velocidad")
    private float velocidad;

    @ManyToOne
    @JoinColumn(name = "id_ruta")
    private Ruta ruta;

    public Tramo(long id_tramo,
            long fid_ubicacion_origen,
            long fid_ubicacion_destino, boolean bloqueado, float distancia,
            float velocidad) {
        this.id_tramo = id_tramo;
        this.fid_ubicacion_origen = fid_ubicacion_origen;
        this.fid_ubicacion_destino = fid_ubicacion_destino;
        this.bloqueado = bloqueado;
        this.distancia = distancia;
        this.velocidad = velocidad;
    }

    public long getId_tramo() {
        return id_tramo;
    }

    public void setId_tramo(long id_tramo) {
        this.id_tramo = id_tramo;
    }

    public long getOrigen() {
        return fid_ubicacion_origen;
    }

    public void setOrigen(long fid_ubicacion_origen) {
        this.fid_ubicacion_origen = fid_ubicacion_origen;
    }

    public long getDestino() {
        return fid_ubicacion_destino;
    }

    public void setDestino(long fid_ubicacion_destino) {
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
