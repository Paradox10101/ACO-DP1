package com.example.backend.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.backend.models.Oficina;
import com.example.backend.models.Region;
import com.example.backend.models.Ubicacion;
import com.example.backend.Repository.OficinaRepository;
import com.example.backend.Repository.RegionRepository;
import com.example.backend.Repository.UbicacionRepository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.Optional;


@Service
public class OficinaService {

    @Autowired
    private OficinaRepository oficinaRepository;

    @Autowired
    private UbicacionRepository ubicacionRepository;

    @Autowired
    private RegionRepository regionRepository;

    // Obtener todas las oficinas
    public List<Oficina> obtenerTodasLasOficinas() {
        return oficinaRepository.findAll();
    }

    // Guardar una nueva oficina
    public Oficina guardarOficina(Oficina oficina) {
        return oficinaRepository.save(oficina);
    }

    // Buscar una oficina por ID
    public Optional<Oficina> buscarOficinaPorId(Long id) {
        return oficinaRepository.findById(id);
    }

    // Actualizar una oficina
    public Oficina actualizarOficina(Oficina oficina) {
        return oficinaRepository.save(oficina);
    }

    // Eliminar una oficina
    public void eliminarOficina(Long id) {
        oficinaRepository.deleteById(id);
    }

    public ArrayList<Oficina> leerOficinasDesdeArchivo(String rutaArchivo) {
        ArrayList<Oficina> oficinas = new ArrayList<>();

        try (BufferedReader lector = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                Oficina oficina = new Oficina();
                Ubicacion ubicacion = new Ubicacion();

                String[] valores = linea.split(",");
                String ubigeo = valores[0];
                String departamento = valores[1];
                String provincia = valores[2];
                Float latitud = Float.parseFloat(valores[3]);
                Float longitud = Float.parseFloat(valores[4]);
                String nombreRegion = valores[5].trim();
                int capacidadMaxima = Integer.parseInt(valores[6]);

                // Configurando la ubicación y oficina basadas en el archivo
                ubicacion.setUbigeo(ubigeo);
                ubicacion.setDepartamento(departamento);
                ubicacion.setProvincia(provincia);
                ubicacion.setLatitud(latitud);
                ubicacion.setLongitud(longitud);

                oficina.setUbicacion(ubicacion);
                oficina.setCapacidadMaxima(capacidadMaxima);

                // Temporarily set the name of the region
                Region regionTemporal = new Region();
                regionTemporal.setNombre(nombreRegion);
                ubicacion.setRegion(regionTemporal);

                oficinas.add(oficina);
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }

        return oficinas;
    }


    public ArrayList<Oficina> cargarOficinasDesdeBD(String rutaArchivo,
            ArrayList<Region> regiones, ArrayList<Ubicacion> ubicaciones) {
        // Leer las oficinas desde el archivo
        ArrayList<Oficina> oficinas = leerOficinasDesdeArchivo(rutaArchivo);

        // Recorrer cada oficina y establecer las ubicaciones y regiones correspondientes
        for (Oficina oficina : oficinas) {
            Ubicacion ubicacion = oficina.getUbicacion();
            Optional<Region> regionSeleccionada = regiones.stream()
                    .filter(regionS -> regionS.getNombre().equals(ubicacion.getRegion().getNombre()))
                    .findFirst();

            if (regionSeleccionada.isPresent()) {
                ubicacion.setRegion(regionSeleccionada.get());
                // Guardar la ubicación si aún no existe en la base de datos
                if (Objects.isNull(ubicacion.getId_ubicacion()) || !ubicacionRepository.existsById(ubicacion.getId_ubicacion())) {
                    ubicacionRepository.save(ubicacion);
                    ubicaciones.add(ubicacion);
                }
            }
            if (Objects.isNull(oficina.getId_oficina()) || !oficinaRepository.existsById(oficina.getId_oficina())) {
                oficinaRepository.save(oficina);
            }
        }

        return oficinas;
    }


}
