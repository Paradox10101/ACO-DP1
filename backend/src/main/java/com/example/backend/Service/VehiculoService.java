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
            //vehiculoDespacho.get().setEstado(EstadoVehiculo.EnRuta);
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
                //vehiculoMayorCapacidad.get().setEstado(EstadoVehiculo.EnRuta);
                //vehiculoMayorCapacidad.get().setAlmacen(null);

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


    public void actualizarEstadoVehiculos(LocalDateTime fechaInicio, LocalDateTime fechaActual, HashMap<String, ArrayList<Ubicacion>> caminos) {
        List<Almacen> almacenes = almacenService.obtenerTodos();
        List<Vehiculo> vehiculos = vehiculoRepository.findAll();
        for (Vehiculo vehiculo : vehiculos) {

            Tramo tramoActualRecorrido = tramoService.obtenerTramoActualVehiculoFecha(fechaActual, vehiculo.getId_vehiculo());
            Mantenimiento mantenimientoPreventivoAProgramar = mantenimientoService.obtenerMantenimientoPreventivoVehiculoFecha(fechaActual.toLocalDate(), vehiculo.getId_vehiculo());
            Mantenimiento mantenimientoPreventivoActual = mantenimientoService.obtenerMantenimientoPreventivoActual(fechaActual, vehiculo.getId_vehiculo());
            Mantenimiento mantenimientoRecurrenteActual = mantenimientoService.obtenerMantenimientoRecurrenteActual(fechaActual, vehiculo.getId_vehiculo());

            //Funcion que solo se encarga de actualizar el mantenimiento preventivo para que tenga una hora de inicio y una hora de fin
            if(mantenimientoPreventivoAProgramar!=null){
                if(almacenes.stream()
                        .anyMatch(almacenS -> almacenS.getUbicacion().getUbigeo().equals(vehiculo.getUbicacionActual().getUbigeo()))){
                    mantenimientoPreventivoAProgramar.setFechaInicio(fechaInicio);
                    mantenimientoPreventivoAProgramar.setFechaFin(
                            fechaInicio.plus(Duration.between(fechaInicio, fechaInicio.toLocalDate().atTime(LocalTime.MAX)).plusDays(2)));
                    vehiculo.setEstado(EstadoVehiculo.EnMantenimiento);
                }
                mantenimientoService.actualizarMantenimiento(mantenimientoPreventivoAProgramar.getId_mantenimiento(),mantenimientoPreventivoAProgramar);
            }

            //Actualizar el estado del vehiculo en el tramo que esta recorriendo, tomara la ubicacion de origen
            if (tramoActualRecorrido != null) {
                vehiculo.setUbicacionActual(tramoActualRecorrido.getUbicacionOrigen());
                vehiculo.setEstado(EstadoVehiculo.EnRuta);
            }
            //El vehiculo no se encuentra en ruta
            else {
                Tramo ultimoTramoRecorrido = tramoService.obtenerUltimoTramoVehiculoFecha(fechaActual, vehiculo.getId_vehiculo());
                //Si toma un valor nulo es porque el vehiculo todavia no ha iniciado nigngun recorrido
                if (ultimoTramoRecorrido != null)
                    vehiculo.setUbicacionActual(ultimoTramoRecorrido.getUbicacionDestino());

                if(mantenimientoRecurrenteActual!=null||mantenimientoPreventivoActual!=null)
                    vehiculo.setEstado(EstadoVehiculo.EnMantenimiento);
                else if(!almacenes.stream()
                        .anyMatch(almacenS -> almacenS.getUbicacion().getUbigeo().equals(vehiculo.getUbicacionActual().getUbigeo()))){
                    ArrayList<Tramo> rutaOptima =  acoService.obtenerMejorRutaDesdeOficinaAAlmacen(fechaActual, oficinaService.obtenerTodasLasOficinas(),
                            caminos, vehiculo.getUbicacionActual() , ubicacionService.obtenerTodasLasUbicaciones(), obtenerTodos(), almacenService.obtenerTodos(), bloqueoService.obtenerBloqueosEntreFechas(fechaActual, fechaActual.plusHours(24*3)));
                    if(rutaOptima!=null){
                        ArrayList<Mantenimiento> mantenimientos = new ArrayList<>();
                        rutaOptima.stream().forEach(tramoS -> {
                            Mantenimiento mantenimiento = new Mantenimiento();
                            mantenimiento.setFechaInicio(tramoS.getFechaFin());
                            mantenimiento.setFechaFin(tramoS.getFechaFin().plusHours(2));
                            mantenimiento.setTipo(TipoMantenimiento.Recurrente);
                            mantenimiento.setVehiculo(vehiculo);
                            mantenimiento.setPendiente(true);
                            mantenimientos.add(mantenimiento);
                        });
                        mantenimientoService.guardarMantenimientos(mantenimientos);
                        tramoService.guardarTramos(rutaOptima);
                    }
                }
                else{
                    vehiculo.setCapacidadUtilizada(0);
                    vehiculo.setEstado(EstadoVehiculo.Disponible);
                }

            }
            vehiculoRepository.saveAll(vehiculos);


        }

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

    List<Vehiculo> hallarVehiculosConCapacidadDisponible(int cantidadSolicitada){
        List<Vehiculo> vehiculos = vehiculoRepository.findVehiculoDisponibleConCapacidadParcialOcupada(cantidadSolicitada, EstadoVehiculo.Disponible);
        if(vehiculos!=null && vehiculos.size()>0)
            return vehiculos;
        else
            return null;

    }

    public void gestionarAveria(Vehiculo vehiculo, TipoAveria tipoAveria, Tramo tramoActual) {
        switch (tipoAveria) {
            case T1:
                // Avería moderada: retraso de 4 horas
                System.out.println("El vehículo " + vehiculo.getCodigo()
                        + " ha sufrido una avería moderada (T1). Retrasando 4 horas.");
                retrasarTramo(tramoActual, 4);
                break;

            case T2:
                // Avería fuerte: el vehículo no puede continuar, intentar reasignar otro
                // vehículo
                System.out.println("El vehículo " + vehiculo.getCodigo()
                        + " ha sufrido una avería fuerte (T2). Deteniendo el vehículo.");
                vehiculo.setEstado(EstadoVehiculo.Averiado);
                // Intentar reasignar otro vehículo  --> REPLANIFICAREMOS
                replanificarRuta(tramoActual);
                break;

            case T3:
                // Avería siniestro: el vehículo queda fuera de operación por 72 horas
                System.out.println("El vehículo " + vehiculo.getCodigo()
                        + " ha sufrido una avería siniestro (T3). Deteniendo el vehículo.");
                // El vehículo queda fuera de servicio por 72 horas
                vehiculo.setEstado(EstadoVehiculo.Averiado);
                vehiculo.setDisponible(false); // Marcar como no disponible

                // Establecer el tiempo de reparación (72 horas desde el momento actual)
                LocalDateTime tiempoReparacion = LocalDateTime.now().plusHours(72);

                // Simulamos que el vehículo estará reparado después de 72 horas (esto se puede almacenar si es necesario)
                System.out.println(
                        "El vehículo " + vehiculo.getCodigo() + " estará fuera de servicio hasta: " + tiempoReparacion);

                // Intentar reasignar otro vehículo para continuar el trayecto --> REPLANIFICAREMOS
                replanificarRuta(tramoActual);
                break;
        }
    }

    // Método para replanificar la ruta desde la ubicación intermedia
    private void replanificarRuta(Tramo tramoActual) {
        System.out.println("Replanificando la ruta desde la ubicación intermedia del tramo " + tramoActual.getId_tramo());

        // Obtener los vehículos disponibles para continuar la ruta, excluyendo el vehículo averiado
        List<Vehiculo> vehiculosDisponibles = obtenerVehiculosDisponibles().stream()
            .filter(vehiculo -> !vehiculo.getId_vehiculo().equals(tramoActual.getVehiculo().getId_vehiculo()))
            .collect(Collectors.toList());

        List<Almacen> almacenesDisponibles = almacenService.obtenerTodos();
        List<Oficina> oficinasDisponibles = oficinaService.obtenerTodasLasOficinas(); // Buscar también en oficinas

        // Replanificar la ruta desde la ubicación donde ocurrió la avería
        Vehiculo nuevoVehiculo = obtenerVehiculoDesdeOficinasOAlmacenes(vehiculosDisponibles, almacenesDisponibles, oficinasDisponibles,
                tramoActual.getCantidadPaquetes(), tramoActual.getUbicacionOrigen().getUbigeo());

        if (nuevoVehiculo != null) {
            System.out.println("Se ha reasignado un nuevo vehículo: " + nuevoVehiculo.getCodigo());
            tramoActual.setVehiculo(nuevoVehiculo);
            nuevoVehiculo.setEstado(EstadoVehiculo.EnRuta);
            nuevoVehiculo.setDisponible(false); // Marcar el nuevo vehículo como no disponible temporalmente
        } else {
            System.out.println("No se pudo reasignar un vehículo para continuar el trayecto. Intentaremos en próximos ciclos.");
            // Se puede manejar un reintento en ciclos posteriores para evitar colapso
        }
    }

    public Vehiculo obtenerVehiculoDesdeOficinasOAlmacenes(List<Vehiculo> vehiculos, List<Almacen> almacenes, List<Oficina> oficinas, int cantidadPaquetes, String ubigeoOrigen) {
        // Primero buscar en almacenes
        Optional<Almacen> almacenSeleccionado = almacenes.stream()
                .filter(almacenS -> almacenS.getUbicacion().getUbigeo().equals(ubigeoOrigen))
                .findFirst();

        if (almacenSeleccionado.isPresent()) {
            Optional<Vehiculo> vehiculoDesdeAlmacen = vehiculos.stream()
                    .filter(vehiculo -> vehiculo.getTipoVehiculo().getCapacidadMaxima() >= cantidadPaquetes
                            && vehiculo.getUbicacionActual().getUbigeo().equals(ubigeoOrigen)
                            && vehiculo.isDisponible()
                            && vehiculo.verificarDisponibilidad(LocalDateTime.now()))
                    .min(Comparator.comparing(vehiculo -> vehiculo.getTipoVehiculo().getCapacidadMaxima()));

            if (vehiculoDesdeAlmacen.isPresent()) {
                return vehiculoDesdeAlmacen.get();
            }
        }

        // Si no se encuentra en almacenes, buscar en oficinas
        Optional<Oficina> oficinaSeleccionada = oficinas.stream()
                .filter(oficina -> oficina.getUbicacion().getUbigeo().equals(ubigeoOrigen))
                .findFirst();

        if (oficinaSeleccionada.isPresent()) {
            Optional<Vehiculo> vehiculoDesdeOficina = vehiculos.stream()
                    .filter(vehiculo -> vehiculo.getTipoVehiculo().getCapacidadMaxima() >= cantidadPaquetes
                            && vehiculo.getUbicacionActual().getUbigeo().equals(ubigeoOrigen)
                            && vehiculo.isDisponible()
                            && vehiculo.verificarDisponibilidad(LocalDateTime.now()))
                    .min(Comparator.comparing(vehiculo -> vehiculo.getTipoVehiculo().getCapacidadMaxima()));

            if (vehiculoDesdeOficina.isPresent()) {
                return vehiculoDesdeOficina.get();
            }
        }

        // Si no se encuentra ningún vehículo adecuado, se retorna null
        return null;
    }

    // Método para retrasar el tramo en caso de avería moderada
    private void retrasarTramo(Tramo tramo, int horas) {
        tramo.setFechaInicio(tramo.getFechaInicio().plusHours(horas));
        tramo.setFechaFin(tramo.getFechaFin().plusHours(horas));
    }

    /*// Método para reasignar otro vehículo en caso de avería grave (T2)
    private void reasignarVehiculo(Tramo tramoActual) {
        List<Vehiculo> vehiculosDisponibles = obtenerVehiculosDisponibles(); // Obtenemos los vehículos disponibles
        List<Almacen> almacenesDisponibles = almacenService.obtenerTodos(); // Obtenemos almacenes disponibles

        // Intentar obtener un nuevo vehículo
        Vehiculo nuevoVehiculo = obtenerVehiculo(vehiculosDisponibles, almacenesDisponibles,
                tramoActual.getCantidadPaquetes(), tramoActual.getUbicacionOrigen().getUbigeo());

        if (nuevoVehiculo != null) {
            System.out.println("Se ha reasignado un nuevo vehículo: " + nuevoVehiculo.getCodigo());

            // Actualizar el tramo con el nuevo vehículo
            tramoActual.setVehiculo(nuevoVehiculo);

            // Ajustar el estado del nuevo vehículo a "En Ruta"
            nuevoVehiculo.setEstado(EstadoVehiculo.EnRuta);
            nuevoVehiculo.setDisponible(false); // Marcar el nuevo vehículo como no disponible en este momento

            // Actualizar la ubicación del nuevo vehículo a la del tramo
            nuevoVehiculo.setUbicacionActual(tramoActual.getUbicacionOrigen());
        } else {
            System.out.println("No se pudo reasignar un vehículo para continuar el trayecto.");
            // Aquí puedes manejar la replanificación o cancelar la entrega si no es posible reasignar
        }
    }*/





}
