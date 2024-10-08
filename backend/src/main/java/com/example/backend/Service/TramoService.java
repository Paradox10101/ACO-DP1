package com.example.backend.Service;

import com.example.backend.models.PlanTransporte;
import com.example.backend.models.Tramo;
import com.example.backend.Repository.TramoRepository;
import com.example.backend.models.Ubicacion;
import com.example.backend.models.Vehiculo;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.function.Function;
@Service
public class TramoService {

    @Autowired
    private TramoRepository tramoRepository;

    public List<Tramo> obtenerTodos() {
        return tramoRepository.findAll();
    }

    public Optional<Tramo> obtenerPorId(Long id) {
        return tramoRepository.findById(id);
    }

    public List<Tramo> obtenerPorPlanTransporte(PlanTransporte planTransporte) {
        return tramoRepository.findByPlanTransporte(planTransporte);
    }

    public Tramo guardar(Tramo tramo) {
        return tramoRepository.save(tramo);
    }

    @Transactional
    public List<Tramo> guardarTramos(List<Tramo> tramos) {
        tramoRepository.saveAll(tramos);
        return tramos;
    }

    public void eliminar(Long id) {
        tramoRepository.deleteById(id);
    }

    public void actualizarEstadoTramos(LocalDateTime fechaInicio, LocalDateTime fechaFin){
        tramoRepository.actualizarTransitados(fechaInicio, fechaFin);
    }

    public Tramo obtenerTramoActualVehiculoFecha(LocalDateTime fechaActual, Long idVehiculo){
        List<Tramo> tramo = tramoRepository.findTramosByFechaAndVehiculo(fechaActual, idVehiculo);
        if(tramo.isEmpty()){
            return null;
        }
        else return tramo.get(0);
    }

    public Tramo obtenerTramoUltimoPedido(Long idVehiculo){
        Optional<Tramo> tramo = tramoRepository.findLastTramoPedidoByVehiculo(idVehiculo);
        if(tramo.isPresent()){
            return tramo.get();
        }
        else return null;
    }

    public Tramo obtenerUltimoTramoVehiculoFecha(LocalDateTime fechaActual, Long idVehiculo){
        Optional<Tramo> tramo = tramoRepository.findLastTramoByFechaAndVehiculo(fechaActual, idVehiculo);

        if (tramo != null && tramo.isPresent()) {
            return tramo.get();
        }
        else return null;
    }

    List<Tramo> hallarRutaVehiculoCapacidadOcupadaParcialConOficina(LocalDateTime fechaInicio, LocalDateTime fechaLimite, Vehiculo vehiculo , Ubicacion ubicacionDestino){
        List<Tramo> tramosRecorrido = tramoRepository.findTramoBetweenFechasAndVehiculo(fechaInicio, fechaLimite, vehiculo);
        if (tramosRecorrido != null && !tramosRecorrido.isEmpty()) {
            for (Tramo tramo : tramosRecorrido) {
                if(tramo.getubicacionOrigen().getUbigeo().equals(ubicacionDestino.getUbigeo())){
                    return tramosRecorrido;
                }
            }
            if(tramosRecorrido.get(tramosRecorrido.size()-1).getubicacionDestino().getUbigeo().equals(ubicacionDestino.getUbigeo()))
                return tramosRecorrido;
        }
        return null;
    }

    List<Tramo> crearRutaTransporteDerivada(List<Tramo>rutaOriginal, Ubicacion ubicacionDestino, int cantidadSolicitada){
        List<Tramo> rutaDerivada = new ArrayList<>();
        for(Tramo tramo : rutaOriginal){
            Tramo nuevoTramo = new Tramo(tramo.getFechaInicio(), tramo.getFechaFin(), tramo.getubicacionOrigen(), tramo.getubicacionDestino(), tramo.getVehiculo(), tramo.getDuracion(), tramo.isTransitado(), tramo.getVelocidad(), tramo.getDistancia(), tramo.isBloqueado());
            nuevoTramo.setCantidadPaquetes(cantidadSolicitada);
            rutaDerivada.add(nuevoTramo);
            if(tramo.getubicacionDestino().getUbigeo().equals(ubicacionDestino.getUbigeo()))break;
        }
        return rutaDerivada;
    }

    public Map<Long, Tramo> obtenerTramosPorFechaYVehiculo(LocalDateTime fechaFin, List<Vehiculo> vehiculos) {
        List<Long> vehiculoIds = vehiculos.stream().map(Vehiculo::getId_vehiculo).collect(Collectors.toList());
        List<Tramo> tramos = tramoRepository.findTramosPorFechaYVehiculos(fechaFin, vehiculoIds);

        return tramos.stream().collect(Collectors.toMap(tramo -> tramo.getVehiculo().getId_vehiculo(), Function.identity()));
    }

    public Map<Long, Tramo> obtenerUltimosTramosPorVehiculo(LocalDateTime fechaFin, List<Vehiculo> vehiculos) {
        List<Long> vehiculoIds = vehiculos.stream().map(Vehiculo::getId_vehiculo).collect(Collectors.toList());
        List<Tramo> ultimosTramos = tramoRepository.findUltimosTramosPorVehiculos(fechaFin, vehiculoIds);

        return ultimosTramos.stream()
        .collect(Collectors.toMap(
            tramo -> tramo.getVehiculo().getId_vehiculo(),  // Usamos el ID del vehiculo como clave
            Function.identity(),  // Mapeamos el Tramo como valor
            (existing, replacement) -> existing  // Resolución de conflicto: mantener el valor existente
        ));
    }

}
