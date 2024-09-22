package com.example.backend.models;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Bloqueo {
    private int id_bloqueo;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Tramo tramoAfectado;

    public Bloqueo(int id_bloqueo, LocalDateTime fechaInicio, LocalDateTime fechaFin, Tramo tramoAfectado) {
        this.id_bloqueo = id_bloqueo;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.tramoAfectado = tramoAfectado;
    }

    public int getId_bloqueo() {
        return id_bloqueo;
    }

    public void setId_bloqueo(int id_bloqueo) {
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

    public static ArrayList<Bloqueo> cargarBloqueosDesdeArchivo(String rutaArchivo) {
        ArrayList<Bloqueo> bloqueos = new ArrayList<>();
        try {
            Path path = Paths.get(rutaArchivo).toAbsolutePath();
            try (BufferedReader br = Files.newBufferedReader(path)) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    String[] valores = linea.split(";");
                    String ubigeoString = valores[0];
                    String fechaString = valores[1];
                    String ubigeoOrigen = ubigeoString.split(" => ")[0].trim();
                    String ubigeoDestino = ubigeoString.split(" => ")[1].trim();
                    String diaMesHoraMinutoInicioString = fechaString.split("==")[0].trim();
                    String diaMesHoraMinutoFinString = fechaString.split("==")[1].trim();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

                    String diaMesInicioString = diaMesHoraMinutoInicioString.split(",")[0];
                    String horaMinutoInicioString = diaMesHoraMinutoInicioString.split(",")[1];
                    String mesInicioString = diaMesInicioString.substring(0, 2);
                    String diaInicioString = diaMesInicioString.substring(2);
                    String fechaHoraInicioString = diaInicioString + "/" +
                            mesInicioString + "/" + String.valueOf(LocalDateTime.now().getYear()) + " "
                            + horaMinutoInicioString;

                    LocalDateTime fechaHoraInicio = LocalDateTime.parse(fechaHoraInicioString, formatter);

                    String diaMesFinString = diaMesHoraMinutoFinString.split(",")[0];
                    String horaMinutoFinString = diaMesHoraMinutoFinString.split(",")[1];
                    String mesFinString = diaMesFinString.substring(0, 2);
                    String diaFinString = diaMesFinString.substring(2);
                    String fechaHoraFinString = diaFinString + "/" +
                            mesFinString + "/" + String.valueOf(LocalDateTime.now().getYear()) + " "
                            + horaMinutoFinString;

                    LocalDateTime fechaHoraFin = LocalDateTime.parse(fechaHoraFinString, formatter);

                    System.out.println(fechaHoraInicio);
                }
            }

        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
        return bloqueos;
    }
}
