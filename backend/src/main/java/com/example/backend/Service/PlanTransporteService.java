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
                                                             HashMap<String, ArrayList<Ubicacion>> caminos){
        List<Ubicacion> ubicaciones = ubicacionService.obtenerTodasLasUbicaciones();
        List<Oficina> oficinas = oficinaService.obtenerTodasLasOficinas();
        List<Bloqueo> bloqueosPeriodoEntrega = bloqueoService.obtenerBloqueosEntreFechas(fechaInicio,
                fechaInicio.plusHours(24*pedido.getOficinaDestino().getUbicacion().getRegion().getDiasLimite()));
        int cantidadSolicitada = pedido.getCantidadPaquetes();
        ArrayList<PlanTransporte> planesTransporte = new ArrayList<>();

        imprimirDatosPedido(pedido);
        //En caso de que la cantidad solicitada sea atendida, no se generaran mas planes de transporte
        while(cantidadSolicitada > 0) {
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

            Vehiculo vehiculoSeleccionado = vehiculoService.obtenerVehiculo(vehiculos, almacenes ,cantidadSolicitada, rutaOptima.get(0).getubicacionOrigen().getUbigeo());
            if(rutaOptima == null || rutaOptima.isEmpty() || vehiculoSeleccionado==null){
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
                imprimirRutasPlanTransporte(planOptimo);

            }
            planesTransporte.add(planOptimo);
        }

        if(planesTransporte.isEmpty() || planesTransporte == null)
            System.out.println("No se encontró una ruta válida para el envío: " + pedido.getId_pedido());

        return planesTransporte;
    }

    public void imprimirDatosPedido(Pedido pedido){
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
        for (Tramo tramo : tramos){
            System.out.println("--------------------------------------------------");
            System.out.println("Ubicación Origen - ID: " + tramo.getubicacionOrigen().getId_ubicacion()
                    + " | Ubigeo: " + tramo.getubicacionOrigen().getUbigeo());
            System.out.println("Region Origen: " + tramo.getubicacionOrigen().getId_ubicacion()
                    + " | Ubigeo: " + tramo.getubicacionOrigen().getRegion().getNombre());
            System.out.println("Ubicación Destino - ID: " + tramo.getubicacionDestino().getId_ubicacion()
                    + " | Ubigeo: " + tramo.getubicacionDestino().getUbigeo());
            System.out.println("Region Destino: " + tramo.getubicacionOrigen().getId_ubicacion()
                    + " | Ubigeo: " + tramo.getubicacionDestino().getRegion().getNombre());
            System.out.println("Distancia: " + tramo.getDistancia() + " km");
            System.out.println("Velocidad: " + tramo.getVelocidad() + " km/h");
            System.out.println("Fecha Inicio Recorrido: " + tramo.getFechaInicio().getDayOfMonth() + "/" + tramo.getFechaInicio().getMonthValue() + "/" + tramo.getFechaInicio().getYear() + " " + tramo.getFechaInicio().getHour() + "h:" + tramo.getFechaInicio().getMinute() + "m");
            System.out.println("Fecha Fin Recorrido: " + tramo.getFechaFin().getDayOfMonth() + "/" + tramo.getFechaFin().getMonthValue() + "/" + tramo.getFechaFin().getYear() + " " + tramo.getFechaFin().getHour() + "h:" + tramo.getFechaFin().getMinute() + "m");
        }
    }



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
}

