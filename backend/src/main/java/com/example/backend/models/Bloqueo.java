package com.example.backend.models;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.springframework.stereotype.Component;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Table;
import jakarta.persistence.OneToOne;

//@AllArgsConstructor
//@NoArgsConstructor
/*@Getter
@Setter*/
//@Component
@Entity
@Table(name = "Bloqueo")
public class Bloqueo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_bloqueo")
    private Long id_bloqueo;

    @Column(name = "fecha_inicio")
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

    @OneToOne
    @JoinColumn(name = "id_tramo", nullable = false)
    private Long fid_tramoAfectado;

    public Bloqueo() {

    }

    public Bloqueo(Long id_bloqueo, LocalDateTime fechaInicio, LocalDateTime fechaFin, Long fid_tramoAfectado) {
        this.id_bloqueo = id_bloqueo;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.fid_tramoAfectado = fid_tramoAfectado;
    }

    public Long getId_bloqueo() {
        return id_bloqueo;
    }

    public void setId_bloqueo(Long id_bloqueo) {
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

    public Long getFidTramoAfectado() {
        return fid_tramoAfectado;
    }

    public void setFidTramoAfectado(Long fid_tramoAfectado) {
        this.fid_tramoAfectado = fid_tramoAfectado;
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
