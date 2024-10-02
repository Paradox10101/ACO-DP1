package com.example.backend.Service;

import com.example.backend.algorithm.AcoService;
import com.example.backend.models.*;


import com.example.backend.Repository.PlanTransporteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlanTransporteService {

    @Autowired
    private PlanTransporteRepository planTransporteRepository;

    @Autowired
    private OficinaService oficinaService;

    @Autowired
    private AcoService  acoService;

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

    // Algo implementado en el service <----
    public PlanTransporte crearRuta(Pedido pedido, List<Almacen> almacenes, HashMap<String, ArrayList<Ubicacion>> caminos, 
            List<Region> regiones, List<Ubicacion> ubicaciones, List<Vehiculo> vehiculos, List<Bloqueo> bloqueos){
        
        
        List<Oficina> oficinas = oficinaService.obtenerTodasLasOficinas();  //obtener oficinas
        //List<Tramo> tramos = new ArrayList<>();

        //List<Almacen> almacenes = new ArrayList<>();

        //List<Oficina> oficinas = new ArrayList<>();
        //List<Tramo> tramos = new ArrayList<>();
        //List<Tramo> rutas = new ArrayList<>();
        System.out.println("-----------------ENTRANDO A EJECUTAR ALGORITMO---------------------------------");
        PlanTransporte planOptimo =  acoService.ejecutar(oficinas, caminos, pedido, regiones, ubicaciones, vehiculos, almacenes, pedido.getCantidadPaquetes(), bloqueos);

        if(planOptimo != null){
            pedido.setEstado(EstadoPedido.Registrado);
            planOptimo.setPedido(pedido);
            planOptimo.setEstado(EstadoPedido.Registrado);
            
            //Falta hallar tramos por plan de transporte
            //List<Tramo> tramosRuta = planOptimo.getTra();
            //actualizarCambiosEnvio(tramosRuta, pedido, oficinas);
            return planOptimo; // Retorna el plan de transporte encontrado
            //planViajeRepository.save(rutaOptima);
            //rutas.add(rutaOptima);
        }else{
            PlanTransporte rutaInvalida = new PlanTransporte(); // Crear una nueva instancia de PlanViaje
            rutaInvalida.setPedido(pedido); // Asignar el envío a la nueva instancia
            //rutas.add(rutaInvalida);
            System.out.println("No se encontró una ruta válida para el envío: " + pedido.getId_pedido());
            return null;
        }

        //return planOptimo;
    }


    public ArrayList<PlanTransporte> definirPlanesTransporte(Pedido pedido, List<Almacen> almacenes, HashMap<String, ArrayList<Ubicacion>> caminos,
                                                List<Region> regiones, List<Ubicacion> ubicaciones, List<Vehiculo> vehiculos, List<Bloqueo> bloqueos){
        ArrayList<PlanTransporte> planesTransporte = new ArrayList<>();
        List<Bloqueo> bloqueosProgramados = bloqueos.stream().
                filter(bloqueoS ->
                        bloqueoS.getFechaInicio().isBefore(pedido.getFechaRegistro().plusHours(24*pedido.getOficinaDestino().getUbicacion().getRegion().getDiasLimite()))
                        && bloqueoS.getFechaFin().isAfter(pedido.getFechaRegistro())).collect(Collectors.toList()); //obtener lista de bloqueos comprendidos en la fechas de planificacion
        int cantidadSolicitada = pedido.getCantidadPaquetes();
        List<Oficina> oficinas = oficinaService.obtenerTodasLasOficinas();  //obtener oficinas

        System.out.println("-----------------ENTRANDO A EJECUTAR ALGORITMO---------------------------------");

        //En caso de que la cantidad solicitada sea atendida, no se generaran mas planes de transporte
        while(cantidadSolicitada > 0) {
            List<Almacen> almacenesConVehiculosDisponibles = almacenes.stream().filter(almacenS -> almacenS.getCantidadVehiculos() > 0).collect(Collectors.toCollection(ArrayList::new));
            List<Vehiculo> vehiculosDisponibles = vehiculos.stream().filter(vehiculoS -> vehiculoS.getEstado() == EstadoVehiculo.Disponible).collect(Collectors.toCollection(ArrayList::new));

            if (almacenesConVehiculosDisponibles.isEmpty() || vehiculosDisponibles.isEmpty()) {
                System.out.println("No se pudo planificar la totalidad de entregas para el pedido con id: " + pedido.getId_pedido() + " y cantidad de paquetes " + pedido.getCantidadPaquetes());
                break;
            }

            if (almacenesConVehiculosDisponibles.stream().anyMatch(almacenSel -> almacenSel.getUbicacion().getUbigeo().equals(pedido.getOficinaDestino().getUbicacion().getUbigeo()))){
                System.out.println("No se genero plan de transporte porque el producto se solicito en el mismo lugar del almacen");
                break;
            }


            PlanTransporte planOptimo =  acoService.ejecutar(oficinas, caminos, pedido, regiones, ubicaciones, vehiculosDisponibles, almacenesConVehiculosDisponibles, cantidadSolicitada, bloqueosProgramados);
            if(planOptimo == null || planOptimo.getVehiculo() == null){
                System.out.println("No se pudo planificar la totalidad de entregas para el pedido con id: " + pedido.getId_pedido() + " y cantidad de paquetes " + pedido.getCantidadPaquetes());
                break;
            }
            else{
                cantidadSolicitada -= planOptimo.getVehiculo().getCapacidadUtilizada();
            }
            planesTransporte.add(planOptimo);
        }

        if(!planesTransporte.isEmpty()){
            pedido.setEstado(EstadoPedido.Registrado);
            planesTransporte.get(0).setPedido(pedido);
            planesTransporte.get(0).setEstado(EstadoPedido.Registrado);

            //Falta hallar tramos por plan de transporte
            //List<Tramo> tramosRuta = planOptimo.getTra();
            //actualizarCambiosEnvio(tramosRuta, pedido, oficinas);
            return planesTransporte; // Retorna el plan de transporte encontrado
            //planViajeRepository.save(rutaOptima);
            //rutas.add(rutaOptima);
        }
        else{
            PlanTransporte rutaInvalida = new PlanTransporte(); // Crear una nueva instancia de PlanViaje
            rutaInvalida.setPedido(pedido); // Asignar el envío a la nueva instancia
            //rutas.add(rutaInvalida);
            System.out.println("No se encontró una ruta válida para el envío: " + pedido.getId_pedido());
        }
        return null;
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
}

