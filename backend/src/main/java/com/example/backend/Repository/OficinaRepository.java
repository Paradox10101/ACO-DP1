package com.example.backend.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.backend.models.Oficina;


public interface OficinaRepository extends JpaRepository<Oficina, Integer> {

    Optional<Oficina> findByCodigo(String codigo);
}
