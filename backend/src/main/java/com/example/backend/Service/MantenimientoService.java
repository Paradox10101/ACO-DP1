package com.example.backend.Service;

import com.example.backend.Repository.MantenimientoRepository;
import com.example.backend.models.Mantenimiento;
import com.example.backend.models.TipoMantenimiento;
import com.example.backend.models.Tramo;
import com.example.backend.models.Vehiculo;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.function.Function;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class MantenimientoService {

    @Autowired
    private MantenimientoRepository mantenimientoRepository;

    // Obtener todos los mantenimientos
    public List<Mantenimiento> obtenerTodosLosMantenimientos() {
        return mantenimientoRepository.findAll();
    }

    // Guardar nuevo mantenimiento
    public Mantenimiento guardarMantenimiento(Mantenimiento mantenimiento) {
        return mantenimientoRepository.save(mantenimiento);
    }

    public List<Mantenimiento> guardarMantenimientos(List<Mantenimiento> mantenimientos) {
        return mantenimientoRepository.saveAll(mantenimientos);
    }

    // Buscar un mantenimiento por ID
    public Optional<Mantenimiento> buscarMantenimientoPorId(Long id) {
        return mantenimientoRepository.findById(id);
    }


    // Eliminar una oficina
    public void eliminarMantenimiento(Long id) {
        mantenimientoRepository.deleteById(id);
    }

    public Mantenimiento obtenerMantenimientoActualVehiculoFecha(LocalDateTime fechaActual, Long idVehiculo){
        Optional<Mantenimiento> mantenimiento = mantenimientoRepository.findMantenimientoByFechaAndVehiculo(fechaActual, idVehiculo);
        if(mantenimiento.isPresent()){
            return mantenimiento.get();
        }
        else return null;
    }


    public Mantenimiento actualizarMantenimiento(Long id, Mantenimiento mantenimientoActualizado) {
        Optional<Mantenimiento> mantenimientoExistente = mantenimientoRepository.findById(id);

        if (mantenimientoExistente.isPresent()) {
            Mantenimiento mantenimiento = mantenimientoExistente.get();
            mantenimiento.setFechaInicio(mantenimientoActualizado.getFechaInicio());
            mantenimiento.setFechaFin(mantenimientoActualizado.getFechaFin());
            mantenimiento.setVehiculo(mantenimientoActualizado.getVehiculo());
            mantenimiento.setTipo(mantenimientoActualizado.getTipo());
            return mantenimientoRepository.save(mantenimiento);
        } else {
            throw new RuntimeException("Mantenimiento no encontrado");
        }
    }

    public Mantenimiento obtenerMantenimientoPreventivoVehiculoFecha(LocalDate fechaActual, Long idVehiculo){

        List<Mantenimiento> mantenimientosPreventivosExistente = mantenimientoRepository.findMantenimientoProgramadoByFechaAndVehiculoAndTipoMantenimiento(fechaActual,idVehiculo, TipoMantenimiento.Preventivo);
        if(mantenimientosPreventivosExistente!=null && !mantenimientosPreventivosExistente.isEmpty()){
            for(Mantenimiento mantenimiento : mantenimientosPreventivosExistente){
                if(!mantenimiento.isPendiente()){
                    mantenimiento.setPendiente(true);
                    actualizarMantenimiento(mantenimiento.getId_mantenimiento(), mantenimiento);
                }
            }
            return mantenimientosPreventivosExistente.get(0);
        }
        return null;
    }

    public Mantenimiento obtenerMantenimientoPreventivoActual(LocalDateTime fechaActual, Long idVehiculo){

        List<Mantenimiento> mantenimientosPreventivosExistente = mantenimientoRepository.findMantenimientoByFechaAndVehiculoAndMantenimientoPreventivo(fechaActual, idVehiculo, TipoMantenimiento.Preventivo);
        if(mantenimientosPreventivosExistente!=null && !mantenimientosPreventivosExistente.isEmpty()){
            for(Mantenimiento mantenimiento : mantenimientosPreventivosExistente){
                if(!mantenimiento.isPendiente()){
                    mantenimiento.setPendiente(true);
                    actualizarMantenimiento(mantenimiento.getId_mantenimiento(), mantenimiento);
                }
            }
            return mantenimientosPreventivosExistente.get(0);
        }
        return null;
    }


    public Mantenimiento obtenerMantenimientoRecurrenteActual(LocalDateTime fechaActual, Long idVehiculo){
        Optional<Mantenimiento> mantenimientoRecurrente = mantenimientoRepository.findMantenimientoRecurrenteByFechaAndVehiculoAndTipoMantenimiento(fechaActual,idVehiculo, TipoMantenimiento.Recurrente);
        if(mantenimientoRecurrente.isPresent())
            return mantenimientoRecurrente.get();
        else
            return null;
    }

    public Map<Long, Mantenimiento> obtenerMantenimientosRecurrentes(LocalDateTime fechaFin, List<Vehiculo> vehiculos) {
        List<Long> vehiculoIds = vehiculos.stream().map(Vehiculo::getId_vehiculo).collect(Collectors.toList());
        List<Mantenimiento> mantenimientos = mantenimientoRepository.findMantenimientosRecurrentesPorVehiculos(fechaFin, vehiculoIds);

        return mantenimientos.stream()
                .collect(Collectors.toMap(
                        mantenimiento -> mantenimiento.getVehiculo().getId_vehiculo(), // Usamos el ID del vehículo como
                                                                                       // clave
                        Function.identity(), // Mapeamos el mantenimiento como valor
                        (existing, replacement) -> existing.getFechaInicio().isAfter(replacement.getFechaInicio())
                                ? existing
                                : replacement // Mantener el más reciente
                ));
    }

    public Map<Long, Mantenimiento> obtenerMantenimientosPreventivos(LocalDateTime fechaFin, List<Vehiculo> vehiculos) {
        List<Long> vehiculoIds = vehiculos.stream().map(Vehiculo::getId_vehiculo).collect(Collectors.toList());
        List<Mantenimiento> mantenimientos = mantenimientoRepository.findMantenimientosPreventivosPorVehiculos(fechaFin, vehiculoIds);

        return mantenimientos.stream().collect(Collectors.toMap(mantenimiento -> mantenimiento.getVehiculo().getId_vehiculo(), Function.identity()));
    }


}
