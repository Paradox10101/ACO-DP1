package com.example.backend.Repository;

import com.example.backend.models.Tramo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TramoRepository extends JpaRepository<Tramo, Long> {
    // Métodos adicionales si es necesario
}
