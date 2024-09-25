package com.example.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend.models.Region;

@Repository
public interface RegionRepository  extends JpaRepository<Region, Long> {
    // Puedes agregar métodos adicionales si es necesario
}