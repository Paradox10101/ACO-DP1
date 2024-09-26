package com.example.backend.Service;

import com.example.backend.Repository.AlmacenRepository;
import com.example.backend.Repository.OficinaRepository;
import com.example.backend.Repository.TipoVehiculoRepository;
import com.example.backend.models.*;
import com.example.backend.Repository.VehiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.backend.Repository.TipoVehiculoRepository;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@Service
public class VehiculoService {

    @Autowired
    private VehiculoRepository vehiculoRepository;

    @Autowired
    private AlmacenRepository almacenRepository;

    @Autowired
    private TipoVehiculoRepository tipoVehiculoRepository;

    public List<Vehiculo> obtenerTodos() {
        return vehiculoRepository.findAll();
    }

    public Optional<Vehiculo> obtenerPorId(Long id) {
        return vehiculoRepository.findById(id);
    }

    public Vehiculo guardar(Vehiculo vehiculo) {
        return vehiculoRepository.save(vehiculo);
    }

    public void eliminar(Long id) {
        vehiculoRepository.deleteById(id);
    }

    public static ArrayList<Vehiculo> cargarVehiculosAlmacenesDesdeArchivo(String rutaArchivo, List<Almacen> almacenes, ArrayList<Vehiculo> vehiculos, List<Oficina> oficinas, List<Ubicacion> ubicaciones, ArrayList<TipoVehiculo> tiposVehiculo) {
        try {
            try (BufferedReader lector = new BufferedReader(new FileReader(rutaArchivo))) {
                String linea;
                Ubicacion ubicacion = new Ubicacion();
                for(int i=0; i<3 ;i++){
                    linea = lector.readLine();
                    TipoVehiculo tipoVehiculo = new TipoVehiculo();
                    String[] valores = linea.split(" ");
                    String categoria = valores[0].trim();
                    int capacidad = Integer.parseInt(valores[1].trim());
                    tipoVehiculo.setNombre(categoria);
                    tipoVehiculo.setCapacidadMaxima(capacidad);
                    tiposVehiculo.add(tipoVehiculo);


                }
                for(int i=0; i<3 ;i++){
                    Almacen almacen = new Almacen();
                    linea = lector.readLine();
                    String provinciaSel = linea.trim().toUpperCase();
                    Optional<Ubicacion> ubicacionSeleccionada = ubicaciones.stream().filter(ubicacionS -> ubicacionS.getProvincia().equals(provinciaSel)).findFirst();
                    if(ubicacionSeleccionada.isPresent()){
                        Long id_ubicacion = ubicacionSeleccionada.get().getId_ubicacion();
                        almacen.setUbicacion(ubicacion);
                        linea = lector.readLine();
                        String[] codigosVehiculos = linea.split(",");
                        almacen.setCantidadVehiculos(codigosVehiculos.length);
                        almacenes.add(almacen);
                        for(String codigoVehiculo : codigosVehiculos){
                            Vehiculo vehiculo = new Vehiculo();
                            String codigoCorregido = codigoVehiculo.trim();

                            for(TipoVehiculo tipoVehiculo : tiposVehiculo){
                                if(tipoVehiculo.getNombre().equals(String.valueOf(codigoCorregido.charAt(0)))){
                                    vehiculo.setTipoVehiculo(tipoVehiculo);
                                    vehiculo.setDistanciaTotal(0);
                                    vehiculo.setCodigo(codigoCorregido);
                                    vehiculos.add(vehiculo);
                                    break;
                                }
                            }

                        }

                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
        return vehiculos;
    }

}
