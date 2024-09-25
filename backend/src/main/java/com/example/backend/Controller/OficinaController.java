package com.example.backend.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.backend.models.Oficina;
import com.example.backend.Service.OficinaService;

import java.util.List;

@RestController
@RequestMapping("/api/oficinas")
public class OficinaController {

    @Autowired
    private OficinaService oficinaService;

    // Endpoint para obtener todas las oficinas
    @GetMapping
    public List<Oficina> obtenerOficinas() {
        return oficinaService.obtenerTodasLasOficinas();
    }

    // Endpoint para guardar una oficina
    @PostMapping
    public Oficina guardarOficina(@RequestBody Oficina oficina) {
        return oficinaService.guardarOficina(oficina);
    }

    // Endpoint para buscar una oficina por ID
    @GetMapping("/{id}")
    public Oficina obtenerOficinaPorId(@PathVariable Long id) {
        return oficinaService.buscarOficinaPorId(id);
    }

    // Endpoint para actualizar una oficina
    @PutMapping("/{id}")
    public Oficina actualizarOficina(@PathVariable Long id, @RequestBody Oficina oficina) {
        Oficina oficinaExistente = oficinaService.buscarOficinaPorId(id);
        if (oficinaExistente != null) {
            oficinaExistente.setCapacidadMaxima(oficina.getCapacidadMaxima());
            oficinaExistente.setCapacidadUtilizada(oficina.getCapacidadUtilizada());
            return oficinaService.actualizarOficina(oficinaExistente);
        }
        return null;
    }

    // Endpoint para eliminar una oficina
    @DeleteMapping("/{id}")
    public void eliminarOficina(@PathVariable Long id) {
        oficinaService.eliminarOficina(id);
    }
}
