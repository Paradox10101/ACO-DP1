package com.example.backend.Controller;

import com.example.backend.models.Bloqueo;
import com.example.backend.Service.BloqueoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/bloqueos")
public class BloqueoController {

    @Autowired
    private BloqueoService bloqueoService;

    @GetMapping
    public List<Bloqueo> obtenerTodos() {
        return bloqueoService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public Optional<Bloqueo> obtenerPorId(@PathVariable Long id) {
        return bloqueoService.obtenerPorId(id);
    }

    @PostMapping
    public Bloqueo guardar(@RequestBody Bloqueo bloqueo) {
        return bloqueoService.guardar(bloqueo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        bloqueoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
