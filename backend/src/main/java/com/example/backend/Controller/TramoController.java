package com.example.backend.Controller;

import com.example.backend.models.Tramo;
import com.example.backend.Service.TramoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tramos")
public class TramoController {

    @Autowired
    private TramoService tramoService;

    @GetMapping
    public List<Tramo> obtenerTodos() {
        return tramoService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public Optional<Tramo> obtenerPorId(@PathVariable Long id) {
        return tramoService.obtenerPorId(id);
    }

@PostMapping
    public Tramo guardar(@RequestBody Tramo tramo) {
        return tramoService.guardar(tramo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        tramoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
