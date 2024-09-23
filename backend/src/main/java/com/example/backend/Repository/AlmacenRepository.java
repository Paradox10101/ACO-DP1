package com.example.backend.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.backend.models.Almacen;
import org.springframework.stereotype.Repository;

@Repository
public interface AlmacenRepository extends JpaRepository<Almacen, Integer> {

    Optional<Almacen> findByCodigo(String codigo);
}
