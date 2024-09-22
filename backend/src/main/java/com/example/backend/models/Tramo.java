package com.example.backend.models;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Tramo {
    private int id_tramo;
    private Ubicacion origen;
    private Ubicacion destino;
    private boolean bloqueado;
    private float distancia;
    private float velocidad;

    public Tramo(int id_tramo, Ubicacion origen, Ubicacion destino, boolean bloqueado, float distancia,
            float velocidad) {
        this.id_tramo = id_tramo;
        this.origen = origen;
        this.destino = destino;
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

    public Ubicacion getOrigen() {
        return origen;
    }

    public void setOrigen(Ubicacion origen) {
        this.origen = origen;
    }

    public Ubicacion getDestino() {
        return destino;
    }

    public void setDestino(Ubicacion destino) {
        this.destino = destino;
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
