package com.example.backend.Repository;

import com.example.backend.models.Bloqueo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BloqueoRepository extends JpaRepository<Bloqueo, Long> {
    // Métodos adicionales si es necesario
}
