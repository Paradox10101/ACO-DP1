package com.example.backend.models;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import jakarta.persistence.Entity;
import java.util.ArrayList;
import jakarta.persistence.Table;

@Entity
@Table(name = "Mantenimiento")
public class Mantenimiento {

    private Long id_mantenimiento;
    private TipoMantenimiento tipo;
    private LocalDateTime fechaProgramada;
    private Time duracion;

    public Mantenimiento(Long id_mantenimiento, TipoMantenimiento tipo, LocalDateTime fechaProgramada, Time duracion) {
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

    public LocalDateTime getFechaProgramada() {
        return fechaProgramada;
    }

    public void setFechaProgramada(LocalDateTime fechaProgramada) {
        this.fechaProgramada = fechaProgramada;
    }

    public Time getDuracion() {
        return duracion;
    }

    public void setDuracion(Time duracion) {
        this.duracion = duracion;
    }

    public static ArrayList<Mantenimiento> cargarMantenimientosDesdeArchivo(String rutaArchivo) {
        ArrayList<Mantenimiento> mantenimientos = new ArrayList<>();
        try {
            Path path = Paths.get(rutaArchivo).toAbsolutePath();
            try (BufferedReader br = Files.newBufferedReader(path)) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    String[] valores = linea.split(":");
                    String anhoMesDiaString = valores[0];
                    String placaString = valores[1];
                    String anhoString = anhoMesDiaString.substring(0, 4);
                    String mesString = anhoMesDiaString.substring(4, 6);
                    String diaString = anhoMesDiaString.substring(6, 8);
                    String fechaString = diaString + "/" + mesString + "/" + anhoString;

                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    Date fecha = formatter.parse(fechaString);

                    System.out.println(fecha);
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
