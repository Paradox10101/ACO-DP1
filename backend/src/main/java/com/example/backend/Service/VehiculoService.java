package com.example.backend.Service;

import com.example.backend.Repository.AlmacenRepository;

import com.example.backend.Repository.TipoVehiculoRepository;
import com.example.backend.models.*;
import com.example.backend.Repository.VehiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@Service
public class VehiculoService {

    @Autowired
    private VehiculoRepository vehiculoRepository;

    @Autowired
    private TipoVehiculoRepository tipoVehiculoRepository;
    
    @Autowired
    private AlmacenRepository   almacenRepository;

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

    public ArrayList<Vehiculo> cargarVehiculosAlmacenesDesdeArchivo(String rutaArchivo, List<Almacen> almacenes, ArrayList<Vehiculo> vehiculos, List<Ubicacion> ubicaciones, ArrayList<TipoVehiculo> tiposVehiculo) {
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
                    tipoVehiculoRepository.save(tipoVehiculo);
                    tiposVehiculo.add(tipoVehiculo);
                }
                for(int i=0; i<3 ;i++){
                    Almacen almacen = new Almacen();
                    linea = lector.readLine();
                    String provinciaSel = linea.trim().toUpperCase();
                    Optional<Ubicacion> ubicacionSeleccionada = ubicaciones.stream().filter(ubicacionS -> ubicacionS.getProvincia().equals(provinciaSel)).findFirst();
                    if(ubicacionSeleccionada.isPresent()){
                        almacen.setUbicacion(ubicacionSeleccionada.get());
                        linea = lector.readLine();
                        String[] codigosVehiculos = linea.split(",");
                        almacen.setCantidadVehiculos(codigosVehiculos.length);
                        almacenRepository.save(almacen);
                        almacenes.add(almacen);
                        for(String codigoVehiculo : codigosVehiculos){
                            Vehiculo vehiculo = new Vehiculo();
                            String codigoCorregido = codigoVehiculo.trim();

                            for(TipoVehiculo tipoVehiculo : tiposVehiculo){
                                if(tipoVehiculo.getNombre().equals(String.valueOf(codigoCorregido.charAt(0)))){
                                    vehiculo.setTipoVehiculo(tipoVehiculo);
                                    vehiculo.setDistanciaTotal(0);
                                    vehiculo.setUbicacionActual(ubicacionSeleccionada.get());
                                    vehiculo.setCodigo(codigoCorregido);
                                    vehiculo.setAlmacen(almacen);
                                    vehiculo.setEstado(EstadoVehiculo.Disponible);
                                    vehiculo.setCapacidadMaxima(vehiculo.getTipoVehiculo().getCapacidadMaxima());
                                    vehiculoRepository.save(vehiculo);
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
