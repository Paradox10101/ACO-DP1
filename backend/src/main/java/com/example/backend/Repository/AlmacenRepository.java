package com.example.backend.Repository;


import org.springframework.data.jpa.repository.JpaRepository;
import com.example.backend.models.Almacen;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlmacenRepository extends JpaRepository<Almacen, Long> {

    @Query("SELECT a FROM Almacen a WHERE a.cantidadVehiculos > 0")
    List<Almacen> findAlmacenesConVehiculosDisponibles();
}
