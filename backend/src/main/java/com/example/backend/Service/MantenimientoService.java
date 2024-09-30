package com.example.backend.Service;

import com.example.backend.Repository.MantenimientoRepository;
import com.example.backend.models.Mantenimiento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


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

    // Buscar un mantenimiento por ID
    public Optional<Mantenimiento> buscarMantenimientoPorId(Long id) {
        return mantenimientoRepository.findById(id);
    }

    // Actualizar un mantenimiento
    public Mantenimiento actualizarMantenimiento(Mantenimiento mantenimiento) {
        return mantenimientoRepository.save(mantenimiento);
    }

    // Eliminar una oficina
    public void eliminarMantenimiento(Long id) {
        mantenimientoRepository.deleteById(id);
    }
}
