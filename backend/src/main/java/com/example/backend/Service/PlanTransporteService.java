package com.example.backend.Service;

import com.example.backend.models.PlanTransporte;
import com.example.backend.Repository.PlanTransporteRepository;
import com.example.backend.models.Oficina;
import com.example.backend.models.Pedido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanTransporteService {

    @Autowired
    private PlanTransporteRepository planTransporteRepository;

    public List<PlanTransporte> obtenerTodosLosPlanes() {
        return planTransporteRepository.findAll();
    }

    public PlanTransporte guardarPlan(PlanTransporte planTransporte) {
        return planTransporteRepository.save(planTransporte);
    }

    public PlanTransporte buscarPorId(Long id) {
        return planTransporteRepository.findById(id).orElse(null);
    }

    public void eliminarPlan(Long id) {
        planTransporteRepository.deleteById(id);
    }
}
