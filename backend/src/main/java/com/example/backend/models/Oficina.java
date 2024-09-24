package com.example.backend.models;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Oficina")
public class Oficina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_oficina")
    private Long id_oficina;

    @Column(name = "fid_ubicacion")
    private Long fid_ubicacion;

    @Column(name = "latitud")
    private double latitud;

    @Column(name = "longitud")
    private double longitud;

    @Column(name = "capacidad_utilizada")
    private int capacidadUtilizada;

    @Column(name = "capacidad_maxima")
    private int capacidadMaxima;

    //no va tener ni longitud y latitud --> servir como prueba
    public Oficina(Long id_oficina, Long fid_ubicacion, int capacidadUtilizada, int capacidadMaxima) {
        this.id_oficina = id_oficina;
        this.fid_ubicacion = fid_ubicacion;
        this.capacidadUtilizada = capacidadUtilizada;
        this.capacidadMaxima = capacidadMaxima;
    }

    public Oficina(Long id_oficina, Long fid_ubicacion, double latitud, double longitud, int capacidadUtilizada,
            int capacidadMaxima) {
        this.id_oficina = id_oficina;
        this.fid_ubicacion = fid_ubicacion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.capacidadUtilizada = capacidadUtilizada;
        this.capacidadMaxima = capacidadMaxima;
    }

    public Oficina() {

    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public Oficina(Long id_oficina, Long fid_ubicacion) {
        this.id_oficina = id_oficina;
        this.fid_ubicacion = fid_ubicacion;
    }
    

    public Long getId_oficina() {
        return id_oficina;
    }

    public void setId_oficina(Long id_oficina) {
        this.id_oficina = id_oficina;
    }

    public Long getFid_ubicacion() {
        return fid_ubicacion;
    }

    public void setFid_ubicacion(Long fid_ubicacion) {
        this.fid_ubicacion = fid_ubicacion;
    }

    public int getCapacidadUtilizada() {
        return capacidadUtilizada;
    }

    public void setCapacidadUtilizada(int capacidadUtilizada) {
        this.capacidadUtilizada = capacidadUtilizada;
    }

    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public void setCapacidadMaxima(int capacidadMaxima) {
        this.capacidadMaxima = capacidadMaxima;
    }

    public static ArrayList<Oficina> cargarOficinasDesdeArchivo(String rutaArchivo) {
        ArrayList<Oficina> oficinas = new ArrayList<>();
        try {
            Path path = Paths.get(rutaArchivo).toAbsolutePath();
            try (BufferedReader br = Files.newBufferedReader(path)) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    String[] valores = linea.split(",");
                    String ubigeo = valores[0];
                    String ciudadOrigen = valores[1];
                    String ciudadDestino = valores[2];
                    Float latitud = Float.parseFloat(valores[3]);
                    Float Longitud = Float.parseFloat(valores[4]);
                    String region = valores[5];
                    //Long id_almacen = Integer.parseInt(valores[6]);

                    System.out.println(linea);
                }
            }

        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
        return oficinas;
    }
}
