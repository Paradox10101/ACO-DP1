package com.example.backend.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.backend.models.Oficina;
import com.example.backend.models.Region;
import com.example.backend.models.Ubicacion;
import com.example.backend.Service.OficinaService;
import com.example.backend.Service.RegionService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/oficinas")
public class OficinaController {

    @Autowired
    private OficinaService oficinaService;

    @Autowired
    private RegionService regionService;

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
    public Optional<Oficina> obtenerOficinaPorId(@PathVariable Long id) {
        return oficinaService.buscarOficinaPorId(id);
    }

    // Endpoint para actualizar una oficina
    @PutMapping("/{id}")
    public Oficina actualizarOficina(@PathVariable Long id, @RequestBody Oficina oficina) {
        Optional<Oficina> oficinaExistente = oficinaService.buscarOficinaPorId(id);
        if (oficinaExistente.isPresent()) {

            oficinaExistente.get().setCapacidadMaxima(oficina.getCapacidadMaxima());
            oficinaExistente.get().setCapacidadUtilizada(oficina.getCapacidadUtilizada());
            return oficinaService.actualizarOficina(oficinaExistente.get());
        }
        return null;
    }

    // Endpoint para eliminar una oficina
    @DeleteMapping("/{id}")
    public void eliminarOficina(@PathVariable Long id) {
        oficinaService.eliminarOficina(id);
    }

    // Agregar un endpoint que permita cargar oficinas desde la base de datos
    @GetMapping("/cargarOficinas")
    public ArrayList<Oficina> cargarOficinas() {
        ArrayList<Region> regiones = regionService.obtenerTodas();
        ArrayList<Ubicacion> ubicaciones = new ArrayList<>();
        HashMap<String, ArrayList<Ubicacion>> caminos = new HashMap<>();
        return oficinaService.cargarOficinasDesdeBD("dataset/Oficinas/c.1inf54.24-2.oficinas.v1.0.txt", regiones, ubicaciones);
    }
}
