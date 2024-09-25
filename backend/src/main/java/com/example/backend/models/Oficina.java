package com.example.backend.models;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
@Table(name = "Oficina")
public class Oficina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_oficina")
    private Long id_oficina;

    @ManyToOne
    @JoinColumn(name = "fid_ubicacion", nullable=false)
    private Ubicacion ubicacion;

    @Column(name = "capacidadUtilizada")
    private int capacidadUtilizada;

    @Column(name = "capacidadMaxima")
    private int capacidadMaxima;


    // Variable estática para manejar el autoincremento de IDs
    private static Long idCounter = 1L;

    //no va tener ni longitud y latitud --> servir como prueba
    public Oficina(Long id_oficina, Ubicacion ubicacion, int capacidadUtilizada, int capacidadMaxima) {
        this.id_oficina = idCounter++;
        this.ubicacion = ubicacion;
        this.capacidadUtilizada = capacidadUtilizada;
        this.capacidadMaxima = capacidadMaxima;
    }

    public Oficina(Long id_oficina, Ubicacion ubicacion, double latitud, double longitud, int capacidadUtilizada,
            int capacidadMaxima) {
        this.id_oficina = idCounter++;
        this.ubicacion = ubicacion;
        this.capacidadUtilizada = capacidadUtilizada;
        this.capacidadMaxima = capacidadMaxima;
    }

    public Oficina() {
        this.id_oficina = idCounter++;
    }


    public Oficina(Long id_oficina, Ubicacion ubicacion) {
        this.id_oficina = id_oficina;
        this.ubicacion = ubicacion;
    }
    

    public Long getId_oficina() {
        return id_oficina;
    }

    public void setId_oficina(Long id_oficina) {
        this.id_oficina = id_oficina;
    }

    public Ubicacion getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(Ubicacion ubicacion) {
        this.ubicacion = ubicacion;
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

    /*public static ArrayList<Oficina> cargarOficinasDesdeArchivo(String rutaArchivo, List<Region> regiones,
                                                                HashMap<String, ArrayList<Ubicacion>> caminos,
                                                                ArrayList<Ubicacion> ubicaciones) {
        ArrayList<Oficina> oficinas = new ArrayList<>();
        try {
            try (BufferedReader lector = new BufferedReader(new FileReader(rutaArchivo))) {
                String linea;
                while ((linea = lector.readLine()) != null) {
                    Oficina oficina  = new Oficina();
                    Ubicacion ubicacion = new Ubicacion();
                    String[] valores = linea.split(",");
                    String ubigeo = valores[0];
                    String departamento = valores[1];
                    String provincia = valores[2];
                    Float latitud = Float.parseFloat(valores[3]);
                    Float longitud = Float.parseFloat(valores[4]);
                    String region = valores[5].trim();
                    int capacidadMaxima = Integer.parseInt(valores[6]);
                    ubicacion.setUbigeo(ubigeo);
                    ubicacion.setDepartamento(departamento);
                    ubicacion.setProvincia(provincia);
                    ubicacion.setLatitud(latitud);
                    ubicacion.setLongitud(longitud);
                    Optional<Region> regionSeleccionada = regiones.stream().filter(regionS -> regionS.getNombre().equals(region)).findFirst();
                    if(regionSeleccionada.isPresent()){
                        ubicacion.setRegion(regionSeleccionada.get());
                        if(!caminos.containsKey(ubigeo)){
                            ubicaciones.add(ubicacion);
                        }
                    }
                    else{
                        continue;
                    }
                    oficina.setUbicacion(ubicacion);
                    oficina.setCapacidadMaxima(capacidadMaxima);
                    caminos.put(ubigeo,new ArrayList<Ubicacion>());
                    oficinas.add(oficina);
                }
            }

        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
        return oficinas;
    }
    */
}
