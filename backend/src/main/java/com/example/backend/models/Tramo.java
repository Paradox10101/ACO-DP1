package com.example.backend.models;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.HashMap;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "Tramo")
public class Tramo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tramo")
    private Long id_tramo;

    @OneToOne
    @JoinColumn(name = "fid_ubicacion_origen", nullable = false)
    private Ubicacion ubicacionOrigen;

    @OneToOne
    @JoinColumn(name = "fid_ubicacion_destino", nullable = false)
    private Ubicacion ubicacionDestino;

    @OneToOne
    @JoinColumn(name = "fid_plan_transporte", nullable = false)
    private PlanTransporte planTransporte;

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


    public Tramo(Long id_tramo,
            Ubicacion ubicacionOrigen,
            Ubicacion ubicacionDestino, boolean bloqueado, float distancia,
            float velocidad, PlanTransporte planTransporte) {
        this.id_tramo = id_tramo;
        this.ubicacionOrigen = ubicacionOrigen;
        this.ubicacionDestino = ubicacionDestino;
        this.bloqueado = bloqueado;
        this.distancia = distancia;
        this.velocidad = velocidad;
        this.planTransporte = planTransporte;
    }

    public Long getId_tramo() {
        return id_tramo;
    }

    public void setId_tramo(Long id_tramo) {
        this.id_tramo = id_tramo;
    }

    public Ubicacion getubicacionOrigen() {
        return ubicacionOrigen;
    }

    public void setubicacionOrigen(Ubicacion ubicacionOrigen) {
        this.ubicacionOrigen = ubicacionOrigen;
    }

    public Ubicacion getubicacionDestino() {
        return ubicacionDestino;
    }

    public void setubicacionDestino(Ubicacion ubicacionDestino) {
        this.ubicacionDestino = ubicacionDestino;
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

    public static ArrayList<Tramo>  cargarTramosDesdeArchivo(String rutaArchivo, HashMap<String, ArrayList<Ubicacion>> caminos) {
        ArrayList<Tramo> tramos = new ArrayList<>();
        try {
            try (BufferedReader lector = new BufferedReader(new FileReader(rutaArchivo))) {
                String linea;
                while ((linea = lector.readLine()) != null) {
                    String[] valores = linea.split(" => ");
                    Ubicacion ubicacion  = new Ubicacion();
                    String ubigeoOrigen = valores[0];
                    String ubigeoDestino = valores[1];
                    ubicacion.setUbigeo(ubigeoOrigen);

                    ArrayList<Ubicacion>listaUbicacionesDestino = caminos.get(ubigeoOrigen);
                    if(listaUbicacionesDestino!=null){
                        listaUbicacionesDestino.add(ubicacion);
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
        return tramos;
    }
}
