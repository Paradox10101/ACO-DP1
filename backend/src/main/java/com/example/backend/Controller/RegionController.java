package com.example.backend.Controller;

import com.example.backend.models.Region;
import com.example.backend.Service.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/regiones")
public class RegionController {

    @Autowired
    private RegionService regionService;

    @GetMapping
    public List<Region> obtenerTodas() {
        return regionService.obtenerTodas();
    }

    @GetMapping("/{id}")
    public Region obtenerPorId(@PathVariable Long id) {
        Region region = regionService.obtenerPorId(id);
        return region;
    }

    @PostMapping
    public Region guardar(@RequestBody Region region) {
        return regionService.guardar(region);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        regionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
