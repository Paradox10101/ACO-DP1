package com.example.backend.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.backend.models.Paquete;


public interface PaqueteRepository extends JpaRepository<Paquete, Integer> {

    Optional<Paquete> findByCodigo(String codigo);
}
