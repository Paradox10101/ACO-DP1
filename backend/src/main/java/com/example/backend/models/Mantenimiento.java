package com.example.backend.models;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "Mantenimiento")
public class Mantenimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mantenimiento")
    private Long id_mantenimiento;
    
    @Column(name = "tipo")
    private TipoMantenimiento tipo;

    @Column(name = "fechaProgramada")
    private Date fechaProgramada;

    @Column(name = "duracion")
    private Time duracion;

    @ManyToOne
    @JoinColumn(name = "fid_vehiculo")
    private Vehiculo vehiculo;

    public Mantenimiento() {
    }

    public Mantenimiento(Long id_mantenimiento, TipoMantenimiento tipo, Date fechaProgramada, Time duracion) {
        this.id_mantenimiento = id_mantenimiento;
        this.tipo = tipo;
        this.fechaProgramada = fechaProgramada;
        this.duracion = duracion;
    }

    public Long getId_mantenimiento() {
        return id_mantenimiento;
    }

    public void setId_mantenimiento(Long id_mantenimiento) {
        this.id_mantenimiento = id_mantenimiento;
    }

    public TipoMantenimiento getTipo() {
        return tipo;
    }

    public void setTipo(TipoMantenimiento tipo) {
        this.tipo = tipo;
    }

    public Date getFechaProgramada() {
        return fechaProgramada;
    }

    public void setFechaProgramada(Date fechaProgramada) {
        this.fechaProgramada = fechaProgramada;
    }

    public Time getDuracion() {
        return duracion;
    }

    public void setDuracion(Time duracion) {
        this.duracion = duracion;
    }

    public static ArrayList<Mantenimiento> cargarMantenimientosDesdeArchivo(String rutaArchivo, List<Vehiculo> vehiculos) {
        ArrayList<Mantenimiento> mantenimientos = new ArrayList<>();
        try {
            Path path = Paths.get(rutaArchivo).toAbsolutePath();
            try (BufferedReader br = Files.newBufferedReader(path)) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    Mantenimiento mantenimiento = new Mantenimiento();
                    String[] valores = linea.split(":");
                    String anhoMesDiaString = valores[0];
                    String codigoString = valores[1];
                    String anhoString = anhoMesDiaString.substring(0, 4);
                    String mesString = anhoMesDiaString.substring(4, 6);
                    String diaString = anhoMesDiaString.substring(6, 8);
                    String fechaString = diaString + "/" + mesString + "/" + anhoString;

                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    Date fecha = formatter.parse(fechaString);
                    Optional<Vehiculo> vehiculoSeleccionado = vehiculos.stream().filter(
                            vehiculoS -> vehiculoS.getCodigo().equals(codigoString)).findFirst();
                    if(vehiculoSeleccionado.isPresent()){
                        mantenimiento.setFechaProgramada(fecha);
                        mantenimientos.add(mantenimiento);
                    }

                }
            }

        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        } catch (ParseException e) {
            System.out.println("Error al leer formato de fecha: " + e.getMessage());
        }
        return mantenimientos;
    }
}
