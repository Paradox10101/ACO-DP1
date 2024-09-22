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
@Entity
public class Tramo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_ubicacion")
    private int id_tramo;

    @ManyToOne
    @JoinColumn(name = "id_ubicacion_origen", nullable = false)
    private int fid_ubicacion_origen;

    @ManyToOne
    @JoinColumn(name = "id_ubicacion_destino", nullable = false)
    private int fid_ubicacion_destino;

    @Column(name="bloqueado")
    private boolean bloqueado;

    @Column(name="distancia")
    private float distancia;

    @Column(name="velocidad")
    private float velocidad;

    @ManyToOne
    @JoinColumn(name = "id_ruta")
    private Ruta ruta;


    public Tramo(int id_tramo, 
            int fid_ubicacion_origen, 
            int fid_ubicacion_destino, boolean bloqueado, float distancia,
            float velocidad) {
        this.id_tramo = id_tramo;
        this.fid_ubicacion_origen = fid_ubicacion_origen;
        this.fid_ubicacion_destino = fid_ubicacion_destino;
        this.bloqueado = bloqueado;
        this.distancia = distancia;
        this.velocidad = velocidad;
    }

    public int getId_tramo() {
        return id_tramo;
    }

    public void setId_tramo(int id_tramo) {
        this.id_tramo = id_tramo;
    }

    public int getOrigen() {
        return fid_ubicacion_origen;
    }

    public void setOrigen(int fid_ubicacion_origen) {
        this.fid_ubicacion_origen = fid_ubicacion_origen;
    }

    public int getDestino() {
        return fid_ubicacion_destino;
    }

    public void setDestino(int fid_ubicacion_destino) {
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
