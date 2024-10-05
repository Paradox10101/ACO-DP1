package com.example.backend.Repository;

import com.example.backend.models.PlanTransporte;
import com.example.backend.models.Tramo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TramoRepository extends JpaRepository<Tramo, Long> {

    List<Tramo> findByPlanTransporte(PlanTransporte planTransporte);
}
