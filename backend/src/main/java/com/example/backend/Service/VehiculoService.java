package com.example.backend.Service;

import com.example.backend.Repository.AlmacenRepository;
import com.example.backend.models.*;
import com.example.backend.Repository.VehiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VehiculoService {

    @Autowired
    private VehiculoRepository vehiculoRepository;

    @Autowired
    private TramoService tramoService;

    @Autowired
    private MantenimientoService mantenimientoService;

    @Autowired
    private AlmacenService almacenService;

    public List<Vehiculo> obtenerTodos() {
        return vehiculoRepository.findAll();
    }

    public List<Vehiculo> obtenerVehiculosDisponibles(){
        /*
        List<Vehiculo> vehiculos = vehiculoRepository.findAll();
        return vehiculos.stream()
                .filter(vehiculo -> vehiculo.getEstado() == EstadoVehiculo.Disponible &&
                        vehiculo.getCapacidadUtilizada() < vehiculo.getTipoVehiculo().getCapacidadMaxima())
                .collect(Collectors.toCollection(ArrayList::new));*/
        EstadoVehiculo estadoDisponible = EstadoVehiculo.Disponible;

        return vehiculoRepository.findVehiculosDisponiblesConCapacidadMenor(estadoDisponible);
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

    public Vehiculo obtenerVehiculo(List<Vehiculo> vehiculos,     List<Almacen>almacenes, int cantidadPaquetes, String ubigeoAlmacen){
        final int[] cantidadPorDespachar = {cantidadPaquetes};
        Optional<Almacen> almacenSeleccionado = almacenes.stream().filter(almacenS -> almacenS.getUbicacion().getUbigeo().equals(ubigeoAlmacen)).findFirst();
        if(!almacenSeleccionado.isPresent())return null;

        Optional<Vehiculo> vehiculoDespacho = vehiculos.stream()
                .filter(vehiculoDS ->  vehiculoDS.getTipoVehiculo().getCapacidadMaxima() >= cantidadPorDespachar[0]
                        && vehiculoDS.getUbicacionActual().getUbigeo().equals(ubigeoAlmacen)
                        && vehiculoDS.isDisponible()
                        && vehiculoDS.verificarDisponibilidad(LocalDateTime.now())) // Verifica si está disponible y libre de averías)
                .min(Comparator.comparing(vehiculoDS -> vehiculoDS.getTipoVehiculo().getCapacidadMaxima()));

        if(vehiculoDespacho.isPresent()){
            vehiculoDespacho.get().setEstado(EstadoVehiculo.EnRuta);
            vehiculoDespacho.get().setAlmacen(null);
            vehiculoDespacho.get().setCapacidadUtilizada(cantidadPaquetes);
            almacenSeleccionado.get().setCantidadVehiculos(almacenSeleccionado.get().getCantidadVehiculos()-1);
            return vehiculoDespacho.get();
        }
        else{
            Optional<Vehiculo> vehiculoMayorCapacidad = vehiculos.stream()
                    .filter(vehiculoDS ->  vehiculoDS.getTipoVehiculo().getCapacidadMaxima() < cantidadPorDespachar[0]
                            && vehiculoDS.getUbicacionActual().getUbigeo().equals(ubigeoAlmacen)
                            && vehiculoDS.isDisponible()
                            && vehiculoDS.verificarDisponibilidad(LocalDateTime.now())) //Aqui se verifica si los vehiculos estan disponibles y si es que es para el momento actual
                    .max(Comparator.comparing(vehiculoDS -> vehiculoDS.getTipoVehiculo().getCapacidadMaxima()));

            if(vehiculoMayorCapacidad.isPresent()){
                vehiculoMayorCapacidad.get().setEstado(EstadoVehiculo.EnRuta);
                vehiculoMayorCapacidad.get().setAlmacen(null);
                vehiculoMayorCapacidad.get().setCapacidadUtilizada(vehiculoMayorCapacidad.get().getCapacidadMaxima());
                almacenSeleccionado.get().setCantidadVehiculos(almacenSeleccionado.get().getCantidadVehiculos()-1);
                return vehiculoMayorCapacidad.get();
            }
        }
        return null;
    }


    public Vehiculo actualizarVehiculo(Long id, Vehiculo vehiculoActualizado) {
        Optional<Vehiculo> vehiculoExistente = vehiculoRepository.findById(id);

        if (vehiculoExistente.isPresent()) {
            Vehiculo vehiculo = vehiculoExistente.get();
            vehiculo.setCapacidadUtilizada(vehiculoActualizado.getCapacidadUtilizada());
            vehiculo.setDisponible(vehiculoActualizado.isDisponible());
            vehiculo.setUbicacionActual(vehiculoActualizado.getUbicacionActual());
            vehiculo.setEstado(vehiculoActualizado.getEstado());
            return vehiculoRepository.save(vehiculo);
        } else {
            throw new RuntimeException("Vehículo no encontrado");
        }
    }


    public void actualizarEstadoVehiculos(LocalDateTime fechaInicio, LocalDateTime fechaFin){
        List<Almacen> almacenes = almacenService.obtenerTodos();
        List<Vehiculo> vehiculos = vehiculoRepository.findAll();
        vehiculos.stream().forEach(vehiculo -> {
            Tramo tramoActual = tramoService.obtenerTramoActualVehiculoFecha(fechaFin, vehiculo.getId_vehiculo());
            if(tramoActual!=null)
                vehiculo.setUbicacionActual(tramoActual.getUbicacionOrigen());

            /*
            Mantenimiento mantenimientoActual = mantenimientoService.obtenerMantenimientoActualVehiculoFecha(fechaFin, vehiculo.getId_vehiculo());
            if(mantenimientoActual!=null){
                tramoActual = tramoService.obtenerUltimoTramoVehiculoFecha(fechaFin,vehiculo.getId_vehiculo());
                if(tramoActual!=null){
                    if(almacenes.contains(tramoActual.getubicacionDestino())){
                        vehiculo.setUbicacionActual(tramoActual.getUbicacionDestino());
                        vehiculo.setEstado(EstadoVehiculo.EnMantenimiento);
                    }
                    //desplazamiento del mantenimiento hasta que llegue a la oficina
                    else{
                        Tramo ultimoTramo = tramoService.obtenerTramoUltimoPedido(vehiculo.getId_vehiculo());
                        if(ultimoTramo!=null){
                            mantenimientoActual.setFechaInicio(ultimoTramo.getFechaInicio());
                            mantenimientoActual.setFechaInicio(ultimoTramo.getFechaInicio().plusHours(2*24));
                        }
                    }
                }
            }
            */


        });
        vehiculoRepository.saveAll(vehiculos);
    }
}
