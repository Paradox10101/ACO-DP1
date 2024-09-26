package com.example.backend.Controller;

import com.example.backend.models.Ubicacion;
import com.example.backend.Service.UbicacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ubicaciones")
public class UbicacionController {

    @Autowired
    private UbicacionService ubicacionService;

    // Obtener todas las ubicaciones
    @GetMapping
    public List<Ubicacion> obtenerTodasLasUbicaciones() {
        return ubicacionService.obtenerTodasLasUbicaciones();
    }

    // Obtener una ubicacion por ID
    @GetMapping("/{id}")
    public ResponseEntity<Ubicacion> obtenerUbicacionPorId(@PathVariable Long id) {
        Optional<Ubicacion> ubicacion = ubicacionService.obtenerUbicacionPorId(id);
        if (ubicacion.isPresent()) {
            return ResponseEntity.ok(ubicacion.get());
        }
        return ResponseEntity.notFound().build();
    }

    // Crear una nueva ubicacion
    @PostMapping
    public ResponseEntity<Ubicacion> crearUbicacion(@RequestBody Ubicacion ubicacion) {
        Ubicacion nuevaUbicacion = ubicacionService.crearUbicacion(ubicacion);
        return ResponseEntity.ok(nuevaUbicacion);
    }

    // Actualizar una ubicacion existente
    @PutMapping("/{id}")
    public ResponseEntity<Ubicacion> actualizarUbicacion(@PathVariable Long id, @RequestBody Ubicacion ubicacion) {
        Ubicacion ubicacionActualizada = ubicacionService.actualizarUbicacion(id, ubicacion);
        if (ubicacionActualizada != null) {
            return ResponseEntity.ok(ubicacionActualizada);
        }
        return ResponseEntity.notFound().build();
    }

    // Eliminar una ubicacion por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUbicacion(@PathVariable Long id) {
        if (ubicacionService.eliminarUbicacion(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
