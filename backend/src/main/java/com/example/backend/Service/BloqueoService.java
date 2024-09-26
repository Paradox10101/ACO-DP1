package com.example.backend.Service;

import com.example.backend.models.Bloqueo;
import com.example.backend.Repository.BloqueoRepository;
import com.example.backend.models.Ubicacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BloqueoService {

    @Autowired
    private BloqueoRepository bloqueoRepository;

    public List<Bloqueo> obtenerTodos() {
        return bloqueoRepository.findAll();
    }

    public Optional<Bloqueo> obtenerPorId(Long id) {
        return bloqueoRepository.findById(id);
    }

    public Bloqueo guardar(Bloqueo bloqueo) {
        return bloqueoRepository.save(bloqueo);
    }

    public void eliminar(Long id) {
        bloqueoRepository.deleteById(id);
    }

    public ArrayList<Bloqueo> cargarBloqueosDesdeArchivo(String rutaArchivo, List<Ubicacion> ubicaciones){
        ArrayList<Bloqueo> bloqueos = new ArrayList<>();
        try {
            Path path = Paths.get(rutaArchivo).toAbsolutePath();
            try (BufferedReader br = Files.newBufferedReader(path)) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    Bloqueo bloqueo = new Bloqueo();
                    String[] valores = linea.split(";");
                    String ubigeoString = valores[0];
                    String fechaString = valores[1];
                    String ubigeoOrigenString = ubigeoString.split(" => ")[0].trim();
                    String ubigeoDestinoString = ubigeoString.split(" => ")[1].trim();
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
                    Optional<Ubicacion> ubicacionOrigenSeleccionada = ubicaciones.stream().filter(ubicacionSOr -> ubicacionSOr.getUbigeo().equals(ubigeoOrigenString)).findFirst();
                    Optional<Ubicacion> ubicacionDestinoSeleccionada = ubicaciones.stream().filter(ubicacionSDes -> ubicacionSDes.getUbigeo().equals(ubigeoDestinoString)).findFirst();
                    if(ubicacionOrigenSeleccionada.isPresent() && ubicacionDestinoSeleccionada.isPresent()){
                        bloqueo.setFechaInicio(fechaHoraInicio);
                        bloqueo.setFechaFin(fechaHoraFin);
                        bloqueo.setUbicacionOrigen(ubicacionOrigenSeleccionada.get());
                        bloqueo.setUbicacionDestino(ubicacionDestinoSeleccionada.get());
                        bloqueoRepository.save(bloqueo);
                        bloqueos.add(bloqueo);
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
        return bloqueos;
    }
}
