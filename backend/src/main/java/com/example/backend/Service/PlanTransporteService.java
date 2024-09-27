package com.example.backend.Service;

import com.example.backend.models.Almacen;
import com.example.backend.models.EstadoPedido;
import com.example.backend.models.Oficina;
import com.example.backend.models.Pedido;
import com.example.backend.models.PlanTransporte;
import com.example.backend.models.Region;
import com.example.backend.models.Tramo;
import com.example.backend.models.Ubicacion;

import jakarta.persistence.Transient;

import com.example.backend.Repository.PlanTransporteRepository;
import com.example.backend.algorithm.Aco;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class PlanTransporteService {

    @Autowired
    private PlanTransporteRepository planTransporteRepository;

    @Autowired
    private OficinaService oficinaService;

    @Autowired
    private Aco aco;

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
            List<Region> regiones, List<Ubicacion> ubicaciones){
        
        
        List<Oficina> oficinas = oficinaService.obtenerTodasLasOficinas();  //obtener oficinas
        //List<Tramo> tramos = new ArrayList<>();

        //List<Almacen> almacenes = new ArrayList<>();

        //List<Oficina> oficinas = new ArrayList<>();
        //List<Tramo> tramos = new ArrayList<>();
        //List<Tramo> rutas = new ArrayList<>();
        System.out.println("-----------------ENTRANDO A EJECUTAR ALGORITMO---------------------------------");
        PlanTransporte planOptimo =  aco.ejecutar(oficinas, caminos, pedido, 0, regiones, ubicaciones);

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
