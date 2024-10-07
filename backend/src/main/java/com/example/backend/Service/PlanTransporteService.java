package com.example.backend.Service;

import com.example.backend.algorithm.AcoService;
import com.example.backend.models.*;


import com.example.backend.Repository.PlanTransporteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlanTransporteService {

    @Autowired
    private PlanTransporteRepository planTransporteRepository;

    @Autowired
    private AlmacenService almacenService;

    @Autowired
    private BloqueoService bloqueoService;

    @Autowired
    private OficinaService oficinaService;

    @Autowired
    private RegionService regionService;

    @Autowired
    private UbicacionService ubicacionService;

    @Autowired
    private VehiculoService vehiculoService;

    @Autowired
    private TramoService tramoService;


    @Autowired
    private AcoService  acoService;
    @Autowired
    private MantenimientoService mantenimientoService;


    public List<PlanTransporte> obtenerTodosLosPlanes() {
        return planTransporteRepository.findAll();
    }

    public PlanTransporte guardarPlan(PlanTransporte planTransporte) {
        if (planTransporte == null) {
            throw new IllegalArgumentException("El plan de transporte no puede ser nulo");
        }

        return planTransporteRepository.save(planTransporte);
    }

    public Optional<PlanTransporte> buscarPorId(Long id) {
        return planTransporteRepository.findById(id);
    }

    public void eliminarPlan(Long id) {
        planTransporteRepository.deleteById(id);
    }


    public ArrayList<PlanTransporte> definirPlanesTransporte(LocalDateTime fechaInicio, Pedido pedido,
                                                             HashMap<String, ArrayList<Ubicacion>> caminos, int semilla, HashMap<Vehiculo, ArrayList<Tramo>> rutasVehiculosDefinidas){

        // Configuramos la semilla en AcoService
        acoService.setSemilla(semilla);
        List<Ubicacion> ubicaciones = ubicacionService.obtenerTodasLasUbicaciones();
        List<Oficina> oficinas = oficinaService.obtenerTodasLasOficinas();
        List<Bloqueo> bloqueosPeriodoEntrega = bloqueoService.obtenerBloqueosEntreFechas(fechaInicio,
                fechaInicio.plusHours(24*pedido.getOficinaDestino().getUbicacion().getRegion().getDiasLimite()));
        int cantidadSolicitada = pedido.getCantidadPaquetes();
        ArrayList<PlanTransporte> planesTransporte = new ArrayList<>();

        //imprimirDatosPedido(pedido);
        System.out.println("Atendiendo pedido ID: " + pedido.getId_pedido() + " con cantidad de paquetes " + pedido.getCantidadPaquetes() + " a oficina ubicada en " + pedido.getOficinaDestino().getUbicacion().getProvincia() + "("+ pedido.getOficinaDestino().getUbicacion().getUbigeo() + ") ..." );

        //En caso de que la cantidad solicitada sea atendida, no se generaran mas planes de transporte
        while(cantidadSolicitada > 0) {
            for(Vehiculo vehiculo: rutasVehiculosDefinidas.keySet()){
                if(vehiculo.getCapacidadUtilizada()>0 && vehiculo.getCapacidadUtilizada()!=vehiculo.getCapacidadMaxima()){
                    List<Tramo> tramosIncluidos = rutasVehiculosDefinidas.get(vehiculo);
                    if((tramosIncluidos.stream().anyMatch(tramo -> tramo.getubicacionOrigen().getUbigeo().equals(pedido.getOficinaDestino().getUbicacion().getUbigeo()))) ||
                            tramosIncluidos.get(tramosIncluidos.size()-1).getubicacionDestino().getUbigeo().equals(pedido.getOficinaDestino().getUbicacion().getUbigeo())
                            && (tramosIncluidos.get(tramosIncluidos.size()-1).getFechaFin().isAfter(fechaInicio.plusHours(24*pedido.getOficinaDestino().getUbicacion().getRegion().getDiasLimite()))
                            ||tramosIncluidos.get(tramosIncluidos.size()-1).getFechaFin().isEqual(fechaInicio.plusHours(24*pedido.getOficinaDestino().getUbicacion().getRegion().getDiasLimite())))){
                        int nuevaCantidadTransporte = (cantidadSolicitada>vehiculo.getCapacidadMaxima()-vehiculo.getCapacidadUtilizada())?vehiculo.getCapacidadMaxima()-vehiculo.getCapacidadUtilizada():cantidadSolicitada;
                        cantidadSolicitada-=nuevaCantidadTransporte;
                        List<Tramo> nuevaRutaDerivada = tramoService.crearRutaTransporteDerivada(rutasVehiculosDefinidas.get(vehiculo), pedido.getOficinaDestino().getUbicacion(), nuevaCantidadTransporte);
                        if(nuevaRutaDerivada!=null && !nuevaRutaDerivada.isEmpty()){
                            PlanTransporte planTransporte = new PlanTransporte();
                            vehiculo.setCapacidadUtilizada(vehiculo.getCapacidadUtilizada()+nuevaCantidadTransporte);
                            planTransporte.setVehiculo(vehiculo);
                            planTransporte.setFechaActalizacion(fechaInicio);
                            planTransporte.setFechaCreacion(fechaInicio);
                            planTransporte.setUbicacionOrigen(nuevaRutaDerivada.get(0).getUbicacionOrigen());
                            planTransporte.setUbicacionDestino(pedido.getOficinaDestino().getUbicacion());
                            vehiculoService.actualizarVehiculo(vehiculo.getId_vehiculo(), vehiculo);
                            guardar(planTransporte);
                            planesTransporte.add(planTransporte);
                        }
                    }
                }
            }
            PlanTransporte planOptimo = new PlanTransporte();
            List<Almacen> almacenes = almacenService.obtenerAlmacenesConVehiculosDisponibles();
            List<Vehiculo> vehiculos = vehiculoService.obtenerVehiculosDisponibles();

            if (almacenes.stream().anyMatch(almacenSel -> almacenSel.getUbicacion().getUbigeo().equals(pedido.getOficinaDestino().getUbicacion().getUbigeo()))){
                System.out.println("No se genero plan de transporte porque el producto se solicito en el mismo lugar del almacen");
                break;
            }

            if (almacenes.isEmpty() || vehiculos.isEmpty()) {
                System.out.println("No se pudo planificar la totalidad de entregas para el pedido con id: " + pedido.getId_pedido() + " y cantidad de paquetes " + pedido.getCantidadPaquetes());
                break;
            }

            ArrayList<Tramo> rutaOptima =  acoService.obtenerMejorRutaAtenderOficinaDesdeAlmacen(fechaInicio, pedido.getOficinaDestino().getUbicacion().getRegion().getDiasLimite()*24,oficinas,
                    caminos, pedido.getOficinaDestino().getUbicacion(), ubicaciones, vehiculos, almacenes, bloqueosPeriodoEntrega);
            if(rutaOptima == null || rutaOptima.isEmpty()){
                System.out.println("No se pudo planificar la totalidad de entregas para el pedido con id: " + pedido.getId_pedido() + " y cantidad de paquetes " + pedido.getCantidadPaquetes());
                break;
            }


            Vehiculo vehiculoSeleccionado = vehiculoService.obtenerVehiculo(vehiculos, almacenes ,cantidadSolicitada, rutaOptima.get(0).getubicacionOrigen().getUbigeo());

            if(vehiculoSeleccionado==null){
                System.out.println("No se pudo planificar la totalidad de entregas para el pedido con id: " + pedido.getId_pedido() + " y cantidad de paquetes " + pedido.getCantidadPaquetes());
                break;
            }
            else{
                vehiculoSeleccionado.setUbicacionActual(rutaOptima.get(0).getubicacionOrigen());
                planOptimo.setVehiculo(vehiculoSeleccionado);
                vehiculoService.actualizarVehiculo(vehiculoSeleccionado.getId_vehiculo(), vehiculoSeleccionado);
                guardar(planOptimo);
                List<Mantenimiento> mantenimientos = new ArrayList<>();

                rutaOptima.stream().forEach(tramoS -> {
                    tramoS.setVehiculo(vehiculoSeleccionado);
                    tramoS.setPlanTransporte(planOptimo);
                    tramoS.setTransitado(false);
                    tramoS.setCantidadPaquetes(vehiculoSeleccionado.getCapacidadUtilizada());
                    Mantenimiento mantenimiento = new Mantenimiento();
                    mantenimiento.setFechaInicio(tramoS.getFechaFin());
                    mantenimiento.setFechaFin(tramoS.getFechaFin().plusHours(2));
                    mantenimiento.setTipo(TipoMantenimiento.Recurrente);
                    mantenimiento.setVehiculo(vehiculoSeleccionado);
                    mantenimiento.setPendiente(true);
                    mantenimientos.add(mantenimiento);
                });

                if(mantenimientos!=null && !mantenimientos.isEmpty()){
                    mantenimientoService.guardarMantenimientos(mantenimientos);
                }
                if(rutaOptima!=null && !rutaOptima.isEmpty()){
                    tramoService.guardarTramos(rutaOptima);
                }

                cantidadSolicitada -= planOptimo.getVehiculo().getCapacidadUtilizada();
                planOptimo.setCantidadTransportada(rutaOptima.get(0).getCantidadPaquetes());
                planOptimo.setUbicacionOrigen(rutaOptima.get(0).getUbicacionOrigen());
                planOptimo.setUbicacionDestino(rutaOptima.get(rutaOptima.size()-1).getUbicacionDestino());
                planOptimo.setFechaCreacion(fechaInicio);
                planOptimo.setFechaActalizacion(fechaInicio);
                planOptimo.setPedido(pedido);

            }
            rutasVehiculosDefinidas.put(vehiculoSeleccionado,rutaOptima);
            planesTransporte.add(planOptimo);
        }

        if(planesTransporte.isEmpty() || planesTransporte == null)
            System.out.println("No se encontró una ruta válida para el envío: " + pedido.getId_pedido());

        guardarTodos(planesTransporte);
        return planesTransporte;
    }

    public void imprimirDatosPedido(Pedido pedido){
        System.out.println("*****************************************************************************************");
        System.out.println("CANTIDAD DE PAQUETES:  " + pedido.getCantidadPaquetes());
        System.out.println("FECHA DE REGISTRO:  " +
                pedido.getFechaRegistro().getDayOfMonth()+
                "/"+
                pedido.getFechaRegistro().getMonthValue()+
                "/"+
                pedido.getFechaRegistro().getYear()+
                " "+
                pedido.getFechaRegistro().getHour()+
                "h:"+
                pedido.getFechaRegistro().getMinute()+
                "m"
        );
        System.out.println("FECHA LIMITE DE ENTREGA:  " +
                pedido.getFechaRegistro().plusHours((long)24*pedido.getOficinaDestino().getUbicacion().getRegion().getDiasLimite()).getDayOfMonth()+
                "/"+
                pedido.getFechaRegistro().plusHours((long)24*pedido.getOficinaDestino().getUbicacion().getRegion().getDiasLimite()).getMonthValue()+
                "/"+
                pedido.getFechaRegistro().plusHours((long)24*pedido.getOficinaDestino().getUbicacion().getRegion().getDiasLimite()).getYear()+
                " "+
                pedido.getFechaRegistro().plusHours((long)24*pedido.getOficinaDestino().getUbicacion().getRegion().getDiasLimite()).getHour()+
                "h:"+
                pedido.getFechaRegistro().plusHours((long)24*pedido.getOficinaDestino().getUbicacion().getRegion().getDiasLimite()).getMinute()+
                "m"
        );
    }

    public void imprimirRutasPlanTransporte(PlanTransporte planTransporte){
        List<Tramo>tramos = tramoService.obtenerPorPlanTransporte(planTransporte);
        //Pedido pedidoIngresado = planTransporte.getPedido();
        //Vehiculo vehiculoSeleccionado = planTransporte.getVehiculo();

        //for (int i = 0; i < tramos.size(); i++){
        for(Tramo tramo : tramos){
            /*
            //Aqui se debe agregar la asignacion de averias
            Tramo tramo = tramos.get(i);
            List<Tramo> tramosRestantes = tramos.subList(i + 1, tramos.size());
            // Verificar la posibilidad de una avería durante el trayecto del tramo
            if (Math.random() < 0.1) {
                gestionarAveria(vehiculoSeleccionado, tramo, tramosRestantes);
                if (vehiculoSeleccionado.getEstado() == EstadoVehiculo.Averiado) {
                    // Si no se puede reasignar vehículo, finalizar la ejecución
                    return;
                }
            }

            // Verificar si la fecha de fin del tramo excede la fecha de entrega estimada
            if (tramo.getFechaFin().isAfter(pedidoIngresado.getFechaEntregaEstimada())) {
                System.out.println("¡Colapso logístico! La fecha de entrega ha sido excedida. Fecha límite: "
                        + pedidoIngresado.getFechaEntregaEstimada() +
                        ", Fecha fin del tramo: " + tramo.getFechaFin());
                // Finalizar la ejecución
                return;
            }

            tramo.setVehiculo(vehiculoSeleccionado);
            */
            //tramo.setCantidadPaquetes(cantidadSolicitada);
            //Aqui termina el codigo de averias

            System.out.println("--------------------------------------------------------------------------------------");
            System.out.println("Ubicación Origen - ID: " + tramo.getubicacionOrigen().getId_ubicacion()
                    + " | Ubigeo: " + tramo.getubicacionOrigen().getUbigeo() + " - Ciudad: " + tramo.getubicacionOrigen().getProvincia());
            System.out.println("Region Origen: ID - " + tramo.getubicacionOrigen().getId_ubicacion()
                    + " | Nombre: " + tramo.getubicacionOrigen().getRegion().getNombre());
            System.out.println("Ubicación Destino - ID: " + tramo.getubicacionDestino().getId_ubicacion()
                    + " | Ubigeo: " + tramo.getubicacionDestino().getUbigeo() + " - Ciudad: " + tramo.getubicacionDestino().getProvincia());
            System.out.println("Region Destino: ID - " + tramo.getubicacionOrigen().getId_ubicacion()
                    + " | Nombre: " + tramo.getubicacionDestino().getRegion().getNombre());
            System.out.println("Distancia: " + tramo.getDistancia() + " km");
            System.out.println("Velocidad: " + tramo.getVelocidad() + " km/h");
            System.out.println("Fecha Inicio Recorrido: " + tramo.getFechaInicio().getDayOfMonth() + "/" + tramo.getFechaInicio().getMonthValue() + "/" + tramo.getFechaInicio().getYear() + " " + tramo.getFechaInicio().getHour() + "h:" + tramo.getFechaInicio().getMinute() + "m");
            System.out.println("Fecha Fin Recorrido: " + tramo.getFechaFin().getDayOfMonth() + "/" + tramo.getFechaFin().getMonthValue() + "/" + tramo.getFechaFin().getYear() + " " + tramo.getFechaFin().getHour() + "h:" + tramo.getFechaFin().getMinute() + "m");
        }
    }
/*
    // Nueva función modular para gestionar averías en PlanTransporteService
    public void gestionarAveria(Vehiculo vehiculo, Tramo tramoActual, TipoAveria tipoAveria) {
        LocalDateTime fechaInicioAveria = tramoActual.getFechaInicio();

        switch (tipoAveria) {
            case T1: // Avería moderada: retrasar 4 horas
                System.out.println(
                        "Vehículo " + vehiculo.getCodigo() + " sufrió avería moderada (T1). Se retrasa 4 horas.");
                retrasarTramos(tramoActual, 4); // Método que retrasa el tramo 4 horas
                break;

            case T2: // Avería fuerte: el vehículo no puede continuar
                System.out.println("Vehículo " + vehiculo.getCodigo() + " sufrió avería fuerte (T2). Se debe detener.");
                vehiculo.setEstado(EstadoVehiculo.Averiado);
                reasignarVehiculo(vehiculo, tramoActual); // Método para reasignar vehículo
                break;

            case T3: // Avería siniestro: el vehículo está fuera de operación por 72 horas
                System.out.println("Vehículo " + vehiculo.getCodigo()
                        + " sufrió siniestro (T3). Está fuera de operación por 72 horas.");
                vehiculo.setEstado(EstadoVehiculo.Averiado);
                // Aquí puedes actualizar lógica de replanificación o cancelación del transporte
                break;

            default:
                break;
        }
    }

    // Función para reasignar vehículo después de una avería grave
    private void reasignarVehiculo(Vehiculo vehiculo, Tramo tramoActual) {
        List<Vehiculo> vehiculosDisponibles = vehiculoService.obtenerVehiculosDisponibles();
        if (vehiculosDisponibles.isEmpty()) {
            System.out.println("No hay vehículos disponibles para continuar la ruta.");
            return;
        }

        Vehiculo nuevoVehiculo = vehiculoService.obtenerVehiculo(vehiculosDisponibles,
                tramoActual.getUbicacionOrigen().getUbigeo());
        if (nuevoVehiculo != null) {
            System.out.println("Reasignado nuevo vehículo: " + nuevoVehiculo.getCodigo());
            tramoActual.setVehiculo(nuevoVehiculo);
        }
    }

    // Función para retrasar los tramos restantes en caso de avería moderada
    private void retrasarTramos(Tramo tramo, int horasRetraso) {
        tramo.setFechaInicio(tramo.getFechaInicio().plusHours(horasRetraso));
        tramo.setFechaFin(tramo.getFechaFin().plusHours(horasRetraso));
    }
*/
    public void actualizarCambiosEnvio(List<Tramo> tramosRuta, Pedido pedido, List<Oficina> oficinas) {
        // Obtener la oficina de destino
        Optional<Oficina> oficinaDestino = oficinaService
                .buscarOficinaPorId(pedido.getOficinaDestino().getId_oficina());

        for (int i = 0; i < tramosRuta.size(); i++) {
            Tramo tramo = tramosRuta.get(i);

            // Verificar si estamos en el primer tramo
            if (i == 0) {
                // Primer tramo, origen es siempre un almacén
                System.out.println(
                        "Almacén de origen: " + pedido.getAlmacen().getId_almacen() + " hasta "
                                + tramo.getubicacionDestino().getId_ubicacion());
            } else {
                // Los tramos siguientes son entre oficinas
                Tramo tramoAnterior = tramosRuta.get(i - 1);
                System.out.println(
                        "De: " + tramoAnterior.getubicacionOrigen().getId_ubicacion() + " a "
                                + tramo.getubicacionDestino().getId_ubicacion());
            }
        }

        // Al llegar al último tramo, verificar si se entrega correctamente a la oficina
        // de destino
        Tramo ultimoTramo = tramosRuta.get(tramosRuta.size() - 1);
        if (ultimoTramo.getubicacionOrigen().getId_ubicacion()
                .equals(oficinaDestino.get().getUbicacion().getId_ubicacion())) {
            System.out.println("Pedido entregado en la oficina destino " + oficinaDestino.get().getId_oficina());
        } else {
            System.out.println("Error: La entrega no coincide con la oficina destino esperada.");
        }
    }

    public PlanTransporte guardar(PlanTransporte planTransporte) {
        return planTransporteRepository.save(planTransporte);
    }

    public List<PlanTransporte> guardarTodos(List<PlanTransporte> planesTransporte) {
        return planTransporteRepository.saveAll(planesTransporte);
    }

    public void imprimirDatosPlanTransporte(PlanTransporte planTransporte) {
        System.out.println("*****************************************************************************************");
        System.out.println("Ubicacion origen: " + planTransporte.getUbicacionOrigen().getProvincia()
                +" | Ubigeo: " + planTransporte.getUbicacionOrigen().getUbigeo());
        System.out.println("Ubicacion destino: " + planTransporte.getUbicacionDestino().getProvincia()
        +" | Ubigeo: " + planTransporte.getUbicacionDestino().getUbigeo());
        System.out.println("Vehiculo trasportador: ID - " + planTransporte.getVehiculo().getId_vehiculo()
        + "\nTipo de vehiculo: " + planTransporte.getVehiculo().getTipoVehiculo().getNombre()
        + "\nCantidad de paquetes transportados: " + planTransporte.getCantidadTransportada()
        + "\nCapacidad maxima: " + planTransporte.getVehiculo().getTipoVehiculo().getCapacidadMaxima());
    }
}

