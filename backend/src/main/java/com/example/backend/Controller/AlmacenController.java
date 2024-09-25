package com.example.backend.Controller;

import com.example.backend.models.Almacen;
import com.example.backend.Service.AlmacenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/almacenes")
public class AlmacenController {

    @Autowired
    private AlmacenService almacenService;

    @GetMapping
    public List<Almacen> obtenerTodos() {
        return almacenService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public Optional<Almacen> obtenerPorId(@PathVariable Long id) {
        Optional<Almacen> almacen = almacenService.obtenerPorId(id);
        return almacen; 
        //OTRA FORMA DE IMPLEMENTARLO
    }

    @PostMapping
    public Almacen guardar(@RequestBody Almacen almacen) {
        return almacenService.guardar(almacen);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        almacenService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
