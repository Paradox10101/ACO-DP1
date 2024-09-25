package com.example.backend.Repository;

import java.util.Optional;

import org.hibernate.mapping.List;
import org.hibernate.mapping.Map;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.backend.models.Oficina;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public interface OficinaRepository extends JpaRepository<Oficina, Long> {
    // Puedes agregar métodos adicionales si es necesario
    Oficina findByNombre(String nombre);
}