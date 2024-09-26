package com.example.backend.Service;

import com.example.backend.models.Ubicacion;
import com.example.backend.Repository.UbicacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UbicacionService {

    @Autowired
    private UbicacionRepository ubicacionRepository;

    // Obtener todas las ubicaciones
    public List<Ubicacion> obtenerTodasLasUbicaciones() {
        return ubicacionRepository.findAll();
    }

    // Obtener una ubicacion por ID
    public Optional<Ubicacion> obtenerUbicacionPorId(Long id) {
        return ubicacionRepository.findById(id);
    }

    // Crear una nueva ubicacion
    public Ubicacion crearUbicacion(Ubicacion ubicacion) {
        return ubicacionRepository.save(ubicacion);
    }

    // Actualizar una ubicacion existente
    public Ubicacion actualizarUbicacion(Long id, Ubicacion ubicacionActualizada) {
        if (ubicacionRepository.existsById(id)) {
            ubicacionActualizada.setId_ubicacion(id);
            return ubicacionRepository.save(ubicacionActualizada);
        }
        return null;
    }

    // Eliminar una ubicacion por ID
    public boolean eliminarUbicacion(Long id) {
        if (ubicacionRepository.existsById(id)) {
            ubicacionRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
