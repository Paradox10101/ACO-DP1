package com.example.backend.Service;

import com.example.backend.Repository.AlmacenRepository;
import com.example.backend.Repository.MantenimientoRepository;
import com.example.backend.algorithm.AcoService;
import com.example.backend.models.*;
import com.example.backend.Repository.VehiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    @Autowired
    private MantenimientoRepository mantenimientoRepository;

    @Autowired
    private AcoService acoService;
    @Autowired
    private OficinaService oficinaService;
    @Autowired
    private BloqueoService bloqueoService;
    @Autowired
    private UbicacionService ubicacionService;

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

    public Vehiculo obtenerVehiculo(List<Vehiculo> vehiculos, List<Almacen> almacenes, int cantidadPaquetes, String ubigeoAlmacen) {
        // Se declara una variable de referencia que contendrá la cantidad de paquetes a despachar
        final int[] cantidadPorDespachar = {cantidadPaquetes};

        // Se busca el almacén que coincide con el ubigeo proporcionado
        Optional<Almacen> almacenSeleccionado = almacenes.stream()
                .filter(almacenS -> almacenS.getUbicacion().getUbigeo().equals(ubigeoAlmacen))
                .findFirst();

        // Si no se encuentra ningún almacén con el ubigeo especificado, se retorna null
        if (!almacenSeleccionado.isPresent()) return null;

        // Se busca el vehículo que tenga suficiente capacidad y esté ubicado en el almacén proporcionado
        // Además, el vehículo debe estar disponible y libre de averías en el momento actual
        Optional<Vehiculo> vehiculoDespacho = vehiculos.stream()
                .filter(vehiculoDS -> vehiculoDS.getTipoVehiculo().getCapacidadMaxima() >= cantidadPorDespachar[0] // Verifica capacidad
                        && vehiculoDS.getUbicacionActual().getUbigeo().equals(ubigeoAlmacen) // Verifica que esté en el almacén correcto
                        && vehiculoDS.isDisponible() // Verifica que esté disponible
                        && vehiculoDS.verificarDisponibilidad(LocalDateTime.now())) // Verifica disponibilidad actual
                .min(Comparator.comparing(vehiculoDS -> vehiculoDS.getTipoVehiculo().getCapacidadMaxima())); // Selecciona el vehículo con la menor capacidad adecuada

        // Si se encuentra un vehículo adecuado
        if (vehiculoDespacho.isPresent()) {
            // Se actualiza el estado del vehículo a "En Ruta" y se desasocia del almacén
            vehiculoDespacho.get().setEstado(EstadoVehiculo.EnRuta);
            vehiculoDespacho.get().setAlmacen(null);

            // Se establece la capacidad utilizada en el vehículo
            vehiculoDespacho.get().setCapacidadUtilizada(cantidadPaquetes);

            // Se reduce en 1 la cantidad de vehículos disponibles en el almacén
            almacenSeleccionado.get().setCantidadVehiculos(almacenSeleccionado.get().getCantidadVehiculos() - 1);

            // Se retorna el vehículo asignado
            return vehiculoDespacho.get();
        } else {
            // Si no se encuentra un vehículo con suficiente capacidad, se intenta buscar uno con mayor capacidad
            Optional<Vehiculo> vehiculoMayorCapacidad = vehiculos.stream()
                    .filter(vehiculoDS -> vehiculoDS.getTipoVehiculo().getCapacidadMaxima() < cantidadPorDespachar[0] // Vehículo con capacidad insuficiente
                            && vehiculoDS.getUbicacionActual().getUbigeo().equals(ubigeoAlmacen) // Ubicación correcta
                            && vehiculoDS.isDisponible() // Verifica que esté disponible
                            && vehiculoDS.verificarDisponibilidad(LocalDateTime.now())) // Verifica disponibilidad actual
                    .max(Comparator.comparing(vehiculoDS -> vehiculoDS.getTipoVehiculo().getCapacidadMaxima())); // Busca el vehículo con mayor capacidad insuficiente

            // Si se encuentra un vehículo con mayor capacidad
            if (vehiculoMayorCapacidad.isPresent()) {
                // Actualiza el estado del vehículo a "En Ruta" y desasocia del almacén
                vehiculoMayorCapacidad.get().setEstado(EstadoVehiculo.EnRuta);
                vehiculoMayorCapacidad.get().setAlmacen(null);

                // Se llena la capacidad máxima del vehículo con la carga
                vehiculoMayorCapacidad.get().setCapacidadUtilizada(vehiculoMayorCapacidad.get().getCapacidadMaxima());

                // Se reduce en 1 la cantidad de vehículos disponibles en el almacén
                almacenSeleccionado.get().setCantidadVehiculos(almacenSeleccionado.get().getCantidadVehiculos() - 1);

                // Se retorna el vehículo asignado
                return vehiculoMayorCapacidad.get();
            }
    }

    // Si no se encuentra ningún vehículo adecuado, se retorna null
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


    public void actualizarEstadoVehiculos(LocalDateTime fechaInicio, LocalDateTime fechaFin, HashMap<String, ArrayList<Ubicacion>> caminos) {
        List<Almacen> almacenes = almacenService.obtenerTodos();
        List<Vehiculo> vehiculos = vehiculoRepository.findAll();
        for (Vehiculo vehiculo : vehiculos) {
            Tramo tramoActualRecorrido = tramoService.obtenerTramoActualVehiculoFecha(fechaFin, vehiculo.getId_vehiculo());
            Mantenimiento mantenimientoPreventivoActual = mantenimientoService.obtenerMantenimientoPreventivoVehiculoFecha(fechaFin.toLocalDate(), vehiculo.getId_vehiculo());
            Mantenimiento mantenimientoRecurrenteActual = mantenimientoService.obtenerMantenimientoRecurrenteActual(fechaFin, vehiculo.getId_vehiculo());
            //Actualizar el estado del vehiculo en el tramo que esta recorriendo, tomara la ubicacion de origen
            if (tramoActualRecorrido != null) {
                vehiculo.setUbicacionActual(tramoActualRecorrido.getUbicacionOrigen());
                vehiculo.setEstado(EstadoVehiculo.EnRuta);
            }
            //El vehiculo no se encuentra en ruta
            else {
                Tramo ultimoTramoRecorrido = tramoService.obtenerUltimoTramoVehiculoFecha(fechaFin, vehiculo.getId_vehiculo());
                //Si toma un valor nulo es porque el vehiculo todavia no ha iniciado nigngun recorrido
                if (ultimoTramoRecorrido != null)
                    vehiculo.setUbicacionActual(ultimoTramoRecorrido.getUbicacionDestino());
            }
            if(mantenimientoPreventivoActual!=null){
                if(almacenes.stream()
                        .anyMatch(almacenS -> almacenS.getUbicacion().getUbigeo().equals(vehiculo.getUbicacionActual().getUbigeo()))){
                    mantenimientoPreventivoActual.setFechaInicio(fechaInicio);
                    mantenimientoPreventivoActual.setFechaFin(
                            fechaInicio.plus(Duration.between(fechaInicio, fechaInicio.toLocalDate().atTime(LocalTime.MAX)).plusDays(2)));
                            vehiculo.setEstado(EstadoVehiculo.EnMantenimiento);
                }
                mantenimientoService.actualizarMantenimiento(mantenimientoPreventivoActual.getId_mantenimiento(),mantenimientoPreventivoActual);
            }
            if(tramoActualRecorrido==null && mantenimientoRecurrenteActual!=null){
                vehiculo.setEstado(EstadoVehiculo.EnMantenimiento);
            }
            //Asignacion de una nueva ruta de retorno, el vehiculo no tiene ninguna entrega pendiente
            if(tramoActualRecorrido==null && mantenimientoRecurrenteActual==null && !almacenes.stream()
                    .anyMatch(almacenS -> almacenS.getUbicacion().getUbigeo().equals(vehiculo.getUbicacionActual().getUbigeo()))){

                ArrayList<Tramo> rutaOptima =  acoService.obtenerMejorRutaDesdeOficinaAAlmacen(fechaFin, oficinaService.obtenerTodasLasOficinas(),
                        caminos, vehiculo.getUbicacionActual() , ubicacionService.obtenerTodasLasUbicaciones(), obtenerTodos(), almacenService.obtenerTodos(), bloqueoService.obtenerBloqueosEntreFechas(fechaFin, fechaFin.plusHours(24*3)));
                vehiculo.setEstado(EstadoVehiculo.EnRuta);

            }
            //Asignacion de una nueva ruta de retorno, el vehiculo no tiene ninguna entrega pendiente
            if(tramoActualRecorrido==null && mantenimientoRecurrenteActual==null && almacenes.stream()
                    .anyMatch(almacenS -> almacenS.getUbicacion().equals(vehiculo.getUbicacionActual()))){
                vehiculo.setEstado(EstadoVehiculo.Disponible);
            }


        }
        vehiculoRepository.saveAll(vehiculos);
    }

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





}
