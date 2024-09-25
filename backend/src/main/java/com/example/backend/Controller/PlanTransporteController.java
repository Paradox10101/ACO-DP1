package com.example.backend.Controller;

import com.example.backend.models.PlanTransporte;
import com.example.backend.Service.PlanTransporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/planTransporte")
public class PlanTransporteController {

    @Autowired
    private PlanTransporteService planTransporteService;

    @GetMapping
    public List<PlanTransporte> obtenerTodosLosPlanes() {
        return planTransporteService.obtenerTodosLosPlanes();
    }

    @PostMapping
    public Optional<PlanTransporte> guardarPlan(@RequestBody PlanTransporte planTransporte) {
        return Optional.of(planTransporteService.guardarPlan(planTransporte));
    }

    @GetMapping("/{id}")
    public Optional<PlanTransporte> obtenerPlanPorId(@PathVariable Long id) {
        return planTransporteService.buscarPorId(id);
    }

    @DeleteMapping("/{id}")
    public void eliminarPlan(@PathVariable Long id) {
        planTransporteService.eliminarPlan(id);
    }
}
