package com.example.backend.Service;

import com.example.backend.Repository.MantenimientoRepository;
import com.example.backend.models.Mantenimiento;
import com.example.backend.models.Tramo;
import com.example.backend.models.Vehiculo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
}
