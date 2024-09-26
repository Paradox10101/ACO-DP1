package com.example.backend.Service;

import com.example.backend.Repository.MantenimientoRepository;
import com.example.backend.Repository.OficinaRepository;
import com.example.backend.Repository.RegionRepository;
import com.example.backend.Repository.UbicacionRepository;
import com.example.backend.models.Mantenimiento;
import com.example.backend.models.Oficina;
import com.example.backend.models.Vehiculo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class MantenimientoService {

    @Autowired
    private MantenimientoRepository mantenimientoRepository;

    // Obtener todos los mantenimientos
    public List<Mantenimiento> obtenerTodosLosMantenimientos() {
        return mantenimientoRepository.findAll();
    }

    // Guardar nuevo mantenimiento
    public Mantenimiento guardarMantenimiento(Mantenimiento mantenimiento) {
        return mantenimientoRepository.save(mantenimiento);
    }

    // Buscar un mantenimiento por ID
    public Optional<Mantenimiento> buscarMantenimientoPorId(Long id) {
        return mantenimientoRepository.findById(id);
    }

    // Actualizar un mantenimiento
    public Mantenimiento actualizarMantenimiento(Mantenimiento mantenimiento) {
        return mantenimientoRepository.save(mantenimiento);
    }

    // Eliminar una oficina
    public void eliminarMantenimiento(Long id) {
        mantenimientoRepository.deleteById(id);
    }


    public ArrayList<Mantenimiento> cargarMantenimientosDesdeArchivo(String rutaArchivo, List<Vehiculo> vehiculos) {
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
                        mantenimiento.setVehiculo(vehiculoSeleccionado.get());
                        mantenimiento.setFechaProgramada(fecha);
                        mantenimientoRepository.save(mantenimiento);
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
