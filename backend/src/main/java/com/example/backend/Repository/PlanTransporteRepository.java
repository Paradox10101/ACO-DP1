package com.example.backend.Repository;

import com.example.backend.models.PlanTransporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
    
@Repository
public interface PlanTransporteRepository extends JpaRepository<PlanTransporte, Long> {
    // Métodos personalizados si es necesario
}