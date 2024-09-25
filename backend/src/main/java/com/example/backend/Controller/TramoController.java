package com.example.backend.Controller;

import com.example.backend.models.Tramo;
import com.example.backend.Service.TramoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public Tramo obtenerPorId(@PathVariable Long id) {
        Tramo tramo = tramoService.obtenerPorId(id);
        return tramo;
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
